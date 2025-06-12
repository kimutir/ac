package com.amvera.cli.command;

import com.amvera.cli.service.CnpgService;
import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(command = "psql", group = "Postgresql commands")
public class PsqlCommand {

    private final CnpgService cnpgService;

    public PsqlCommand(CnpgService cnpgService) {
        this.cnpgService = cnpgService;
    }

    @Command(command = "")
    public void get() {
        cnpgService.renderTable();
    }

    @Command(command = "backup create")
    public void createBackup(
            @Option(longNames = "slug", shortNames = 's') String slug,
            @Option(longNames = "description", shortNames = 'd') String description
    ) {
        cnpgService.createBackup(slug, description);
    }

    @Command(command = "backup delete")
    public void deleteBackup(
            @Option(longNames = "slug", shortNames = 's') String slug,
            @Option(longNames = "backup", shortNames = 'b') String backupName
    ) {
        cnpgService.deleteBackup(slug, backupName);
    }

    @Command(command = "scheduled")
    public void scheduledBackup(
            @Option(longNames = "slug", shortNames = 's', required = true) String slug,
            @Option(longNames = "enable", shortNames = 'e', required = true) Boolean enable
    ) {
        System.out.println(enable);
        System.out.println(slug);
        cnpgService.update(slug, enable);
    }

    /*
    would you like to disable/enable scheduled?
     */

    @Command(command = "backup list", alias = "psql backup ls")
    public void getBackupList(
            @Option(longNames = "slug", shortNames = 's') String slug
    ) {
        cnpgService.renderBackupsTable(slug);
    }

    @Command(command = "restore")
    public void restore(
            @Option(longNames = "source", shortNames = 's') String oldSlug,
            @Option(longNames = "target", shortNames = 't') String newSlug,
            @Option(longNames = "backup", shortNames = 'b') String backupName
    ) {
        cnpgService.restore(newSlug, oldSlug, backupName);
    }



}
