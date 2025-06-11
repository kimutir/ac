package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ScalePostRequest;
import com.amvera.cli.dto.project.cnpg.*;
import com.amvera.cli.utils.AmveraInput;
import com.amvera.cli.utils.CnpgResourceStatus;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.Tariff;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.CnpgTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class CnpgService {

    private final HttpCustomClient client;
    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;

    @Autowired
    public CnpgService(HttpCustomClient client, AmveraTable table, ShellHelper helper, AmveraSelector selector, AmveraInput input) {
        this.client = client;
        this.table = table;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
    }

    public void renderTable() {
        List<ProjectResponse> cnpgList = getAllRequest();
        if (cnpgList.isEmpty()) {
            helper.printWarning("No postgres clusters found. You can start with 'amvera create psql'");
        } else {
            helper.println(table.projects(cnpgList));
        }
    }

    public void renderBackupsTable(String slug) {
        ProjectResponse cnpg = findOrSelect(slug);
        List<CnpgBackupResponse> backupList = getBackupList(cnpg.getSlug());

        if (backupList.isEmpty()) {
            helper.printWarning("No backups found for slug " + slug);
        } else {
            helper.println(table.cnpgBackups(backupList));
        }
    }

    public void createBackup(String slug, String description) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (description == null) {
            description = input.notBlankOrNullInput("Enter backup description: ");
        }

        CnpgBackupResponse backup = createBackupRequest(cnpg.getSlug(), description);

        helper.println(String.format("Started backup process. New backup name: %s", backup.getName()));
    }

    public void deleteBackup(String slug, String backupName) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (backupName == null) {
            backupName = selectBackup(cnpg.getSlug()).getName();
        }

        deleteBackupRequest(cnpg.getSlug(), backupName);

        helper.println("Backup has been deleted.");
    }

    public void restore(String newSlug, String oldSlug, String backupName) {
        ProjectResponse cnpg = findOrSelect(oldSlug);

        if (backupName == null) {
            CnpgBackupResponse backup = selectBackup(
                    cnpg.getSlug(),
                    b -> !b.getStatus().equals(CnpgResourceStatus.CREATED)
            );
            backupName = backup.getName();
        }

        if (newSlug == null) {
            newSlug = input.notBlankOrNullInput("Enter new postgresql cluster name: ");
        }

        String restoredSlug = restoreRequest(newSlug, cnpg.getSlug(), backupName).serviceSlug();

        CnpgResponse restored = findBySlugDetailedRequest(restoredSlug);
        Tariff tariff = Tariff.value(getTariffRequest(restoredSlug).id());

        helper.println(table.singleEntityTable(new CnpgTableModel(restored, tariff)));
    }

    public List<ProjectResponse> getAllRequest() {
        ResponseEntity<ProjectListResponse> response = client.postgresql()
                .get().retrieve()
                .toEntity(ProjectListResponse.class);

        if (response.getStatusCode().isError()) {
            // todo: throw exception
        }

        return response.getBody().getServices();
    }

    public ResponseEntity<Void> deleteRequest(String slug) {
        return client.postgresql()
                .delete()
                .uri("/{slug}", slug)
                .retrieve()
                .toBodilessEntity();
    }

    public ResponseEntity<CnpgResponse> createRequest(CnpgPostRequest request) {
        return client.postgresql()
                .post()
                .body(request)
                .retrieve()
                .toEntity(CnpgResponse.class);
    }

    public ResponseEntity<Void> scaleRequest(String slug, int instances) {
        return client.postgresql()
                .post().uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(instances))
                .retrieve()
                .toBodilessEntity();
    }

    public CnpgBackupResponse createBackupRequest(String slug, String description) {
        ResponseEntity<CnpgBackupResponse> response = client.postgresql()
                .post().uri("/backup")
                .body(new CnpgBackupPostRequest(slug, description))
                .retrieve()
                .toEntity(CnpgBackupResponse.class);

        return response.getBody();
    }

    public ResponseEntity<Void> deleteBackupRequest(String slug, String backupName) {
        return client.postgresql()
                .delete().uri("/backup/{serviceSlug}/{backupName}", slug, backupName)
                .retrieve()
                .toBodilessEntity();
    }

    public CnpgRestoreResponse restoreRequest(String newSlug, String oldSlug, String backupName) {
        ResponseEntity<CnpgRestoreResponse>  response = client.postgresql()
                .post().uri("/restore")
                .body(new CnpgRestorePostRequest(newSlug, oldSlug, backupName))
                .retrieve()
                .toEntity(CnpgRestoreResponse.class);

        return response.getBody();
    }

    public List<CnpgBackupResponse> getBackupList(String slug) {
        ResponseEntity<List<CnpgBackupResponse>> response = client.postgresql()
                .get().uri("/backup/{slug}", slug)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        return response.getBody();
    }

    public CnpgResponse findBySlugDetailedRequest(String slug) {
        ResponseEntity<CnpgResponse> response = client.postgresql().get()
                .uri("/{slug}/details", slug)
                .retrieve()
                .toEntity(CnpgResponse.class);

        return response.getBody();
    }

    public ProjectResponse findOrSelect(String slug) {
        return slug == null ? select() : findBySlugRequest(slug);
    }

    public ProjectResponse findBySlugRequest(String slug) {
        ResponseEntity<ProjectResponse> response = client.project().get()
                .uri("/{slug", slug)
                .retrieve()
                .toEntity(ProjectResponse.class);

        return response.getBody();
    }

    public ProjectResponse select() {
        List<SelectorItem<ProjectSelectItem>> projectList = getAllRequest()
                .stream()
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select postgresql cluster: ", true).getProject();
    }

    public CnpgBackupResponse selectBackup(String slug) {
        List<SelectorItem<CnpgBackupResponse>> projectList = getBackupList(slug)
                .stream()
                .map(CnpgBackupResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select postgresql backup: ", true);
    }

    public CnpgBackupResponse selectBackup(String slug, Predicate<CnpgBackupResponse> predicate) {
        List<SelectorItem<CnpgBackupResponse>> projectList = getBackupList(slug)
                .stream()
                .filter(predicate)
                .map(CnpgBackupResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select postgresql backup: ", true);
    }

    public TariffResponse getTariffRequest(String slug) {
        ResponseEntity<TariffResponse> response = client.project().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .toEntity(TariffResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }

}
