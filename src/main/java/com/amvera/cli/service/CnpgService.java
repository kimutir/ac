package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.cnpg.*;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.input.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.CnpgTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

@Service
public class CnpgService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;
    private final AmveraHttpClient client;
    private final Endpoints endpoints;

    @Autowired
    public CnpgService(
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector,
            AmveraInput input,
            AmveraHttpClient client,
            Endpoints endpoints
    ) {
        this.table = table;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
        this.client = client;
        this.endpoints = endpoints;
    }

    // todo: check
    public void renderTable() {
        List<ProjectResponse> cnpgList = client.get(
                URI.create(endpoints.postgresql()),
                ProjectListResponse.class,
                "Error when retrieving postgres list"
        ).getServices();

        if (cnpgList.isEmpty())
            throw new EmptyValueException("No postgres clusters found. You can start with 'amvera create psql'");

        helper.println(table.projects(cnpgList));
    }

    public void renderBackupsTable(String slug) {
        ProjectResponse cnpg = findOrSelect(slug);
        List<CnpgBackupResponse> backupList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/backup/{slug}").build(cnpg.getSlug()),
                new ParameterizedTypeReference<>() {
                },
                "Error when retrieving postgres backup list"
        );

        if (backupList.isEmpty()) throw new EmptyValueException("No backups found for " + slug);

        helper.println(table.cnpgBackups(backupList));
    }

    public void createBackup(String slug, String description) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (description == null) {
            description = input.notBlankOrNullInput("Enter backup description: ");
        }

        CnpgBackupResponse backup = client.post(
                URI.create(endpoints.postgresql() + "/backup"),
                CnpgBackupResponse.class,
                new CnpgBackupPostRequest(cnpg.getSlug(), description),
                "Backup process failed"
        );

        helper.println(String.format("Started backup process. New backup name: %s", backup.getName()));
    }

    public void deleteBackup(String slug, String backupName) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (backupName == null) {
            backupName = selectBackup(cnpg.getSlug()).getName();
        }

        client.delete(
                UriComponentsBuilder
                        .fromUriString(endpoints.postgresql() + "/backup/{serviceSlug}/{backupName}")
                        .build(slug, backupName),
                String.format("%s backup deletion failed", backupName)
        );

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

        String restoredSlug = client.post(
                URI.create(endpoints.postgresql() + "/restore"),
                CnpgRestoreResponse.class,
                new CnpgRestorePostRequest(newSlug, oldSlug, backupName),
                String.format("Unable to restore postgres from %s backup", backupName)
        ).serviceSlug();

        CnpgResponse restored = client.get(
                UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/{slug}/details").build(restoredSlug),
                CnpgResponse.class,
                String.format("Unable to find '%s' postgres detailed info", restoredSlug)
        );

        Tariff tariff = Tariff.value(
                client.get(
                        UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/{slug}/tariff").build(restoredSlug),
                        TariffResponse.class,
                        "Error when getting postgres tariff"
                ).id()
        );

        helper.println(table.singleEntityTable(new CnpgTableModel(restored, tariff)));
    }

    public void update(String slug, Boolean isEnabled) {
        ProjectResponse project = findOrSelect(slug);

        CnpgResponse cnpg = client.get(
                UriComponentsBuilder
                        .fromUriString(endpoints.postgresql() + "/{slug}/details")
                        .build(project.getSlug()),
                CnpgResponse.class,
                String.format("Unable to find '%s' postgres detailed info", project.getSlug())
        );

        if (cnpg.isScheduledBackupEnabled() == isEnabled) throw new RuntimeException("The same value");

        cnpg = client.put(
                URI.create(endpoints.postgresql()),
                CnpgResponse.class,
                new CnpgPutRequest(slug, isEnabled),
                "Error when updating postgres"
        );

        Tariff tariff = Tariff.value(
                client.get(
                        UriComponentsBuilder
                                .fromUriString(endpoints.postgresql() + "/{slug}/tariff")
                                .build(project.getSlug()),
                        TariffResponse.class,
                        "Error when getting postgres tariff"
                ).id()
        );

        helper.println(table.singleEntityTable(new CnpgTableModel(cnpg, tariff)));
    }

    public ProjectResponse findOrSelect(String slug) {
        return slug == null ? select() : client.get(
                UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/{slug}").build(slug),
                ProjectResponse.class,
                String.format("Unable to find '%s' postgres", slug)
        );
    }

    public ProjectResponse select() {
        List<SelectorItem<ProjectSelectItem>> projectList = client
                .get(
                        URI.create(endpoints.postgresql()),
                        ProjectListResponse.class,
                        "Error when retrieving postgres list"
                )
                .getServices()
                .stream()
                .map(ProjectResponse::toSelectorItem).toList();

        if (projectList.isEmpty()) {
            throw new EmptyValueException("No postgres clusters found. You can start with 'amvera create psql'");
        }

        return selector.singleSelector(projectList, "Select postgresql cluster: ", true).getProject();
    }

    public CnpgBackupResponse selectBackup(String slug) {
        return selectBackup(slug, b -> true);
    }

    public CnpgBackupResponse selectBackup(String slug, Predicate<CnpgBackupResponse> predicate) {
        List<SelectorItem<CnpgBackupResponse>> backupList = client.get(
                        UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/backup/{slug}").build(slug),
                        new ParameterizedTypeReference<List<CnpgBackupResponse>>() {
                        },
                        "Error when retrieving postgres backup list"
                )
                .stream()
                .filter(predicate)
                .map(CnpgBackupResponse::toSelectorItem).toList();

        if (backupList.isEmpty()) throw new EmptyValueException("No backups found for " + slug);

        return selector.singleSelector(backupList, "Select postgresql backup: ", true);
    }

}
