package com.amvera.cli.service;

import com.amvera.cli.client.CnpgClient;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.cnpg.CnpgBackupResponse;
import com.amvera.cli.dto.project.cnpg.CnpgResourceStatus;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.input.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.CnpgTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class CnpgService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;
    private final CnpgClient cnpgClient;

    @Autowired
    public CnpgService(
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector,
            AmveraInput input,
            CnpgClient cnpgClient
    ) {
        this.table = table;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
        this.cnpgClient = cnpgClient;
    }

    public void renderTable() {
        List<ProjectResponse> cnpgList = cnpgClient.getAll();

        if (cnpgList.isEmpty())
            throw new EmptyValueException("No postgres clusters found. You can start with 'amvera create psql'");

        helper.println(table.projects(cnpgList));
    }

    public void renderBackupsTable(String slug) {
        ProjectResponse cnpg = findOrSelect(slug);
        List<CnpgBackupResponse> backupList = cnpgClient.getBackupList(cnpg.getSlug());

        if (backupList.isEmpty()) throw new EmptyValueException("No backups found for " + slug);

        helper.println(table.cnpgBackups(backupList));
    }

    public void createBackup(String slug, String description) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (description == null) {
            description = input.notBlankOrNullInput("Enter backup description: ");
        }

        CnpgBackupResponse backup = cnpgClient.createBackup(cnpg.getSlug(), description);

        helper.println(String.format("Started backup process. New backup name: %s", backup.getName()));
    }

    public void deleteBackup(String slug, String backupName) {
        ProjectResponse cnpg = findOrSelect(slug);

        if (backupName == null) {
            backupName = selectBackup(cnpg.getSlug()).getName();
        }

        cnpgClient.deleteBackup(cnpg.getSlug(), backupName);

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

        String restoredSlug = cnpgClient.restore(newSlug, cnpg.getSlug(), backupName).serviceSlug();

        CnpgResponse restored = cnpgClient.getDetails(restoredSlug);
        Tariff tariff = Tariff.value(cnpgClient.getTariff(restoredSlug).id());

        helper.println(table.singleEntityTable(new CnpgTableModel(restored, tariff)));
    }

    public void update(String slug, Boolean isEnabled) {
        ProjectResponse project = findOrSelect(slug);
        CnpgResponse cnpg = cnpgClient.getDetails(project.getSlug());

        if (cnpg.isScheduledBackupEnabled() == isEnabled) {
            System.out.println("the same");
            throw new RuntimeException("The same value");
        }

        cnpg = cnpgClient.update(slug, isEnabled);
        Tariff tariff = Tariff.value(cnpgClient.getTariff(slug).id());

        helper.println(table.singleEntityTable(new CnpgTableModel(cnpg, tariff)));
    }

    public ProjectResponse findOrSelect(String slug) {
        return slug == null ? select() : cnpgClient.get(slug);
    }

    public ProjectResponse select() {
        List<SelectorItem<ProjectSelectItem>> projectList = cnpgClient.getAll()
                .stream()
                .map(ProjectResponse::toSelectorItem).toList();

        if (projectList.isEmpty()) {
            throw new EmptyValueException("No postgres clusters found. You can start with 'amvera create psql'");
        }

        return selector.singleSelector(projectList, "Select postgresql cluster: ", true).getProject();
    }

    public CnpgBackupResponse selectBackup(String slug) {
        List<SelectorItem<CnpgBackupResponse>> backupList = cnpgClient.getBackupList(slug)
                .stream()
                .map(CnpgBackupResponse::toSelectorItem).toList();

        if (backupList.isEmpty()) throw new EmptyValueException("No backups found for " + slug);

        return selector.singleSelector(backupList, "Select postgresql backup: ", true);
    }

    public CnpgBackupResponse selectBackup(String slug, Predicate<CnpgBackupResponse> predicate) {
        List<SelectorItem<CnpgBackupResponse>> backupList = cnpgClient.getBackupList(slug)
                .stream()
                .filter(predicate)
                .map(CnpgBackupResponse::toSelectorItem).toList();

        if (backupList.isEmpty()) throw new EmptyValueException("No backups found for " + slug);

        return selector.singleSelector(backupList, "Select postgresql backup: ", true);
    }

}
