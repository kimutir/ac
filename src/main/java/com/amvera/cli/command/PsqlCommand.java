package com.amvera.cli.command;

import com.amvera.cli.service.CnpgService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "psql", alias = "psql", group = "Postgresql commands")
public class PsqlCommand {

    private final CnpgService cnpgService;

    public PsqlCommand(CnpgService cnpgService) {
        this.cnpgService = cnpgService;
    }

    @Command(command = "", description = "Returns all postgresql clusters")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void get() {
        cnpgService.renderTable();
    }

    @Command(command = "backup create", description = "Creates postgresql backup")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void createBackup(
            @Option(longNames = "slug", shortNames = 's') String slug,
            @Option(longNames = "description", shortNames = 'd') String description
    ) {
        cnpgService.createBackup(slug, description);
    }

    @Command(command = "backup delete", description = "Deletes postgresql backup")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void deleteBackup(
            @Option(longNames = "slug", shortNames = 's') String slug,
            @Option(longNames = "backup", shortNames = 'b') String backupName
    ) {
        cnpgService.deleteBackup(slug, backupName);
    }

    @Command(command = "scheduled", description = "Disables/enables postgresql scheduled backups")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void scheduledBackup(
            @Option(longNames = "slug", shortNames = 's', required = true) String slug,
            @Option(longNames = "enable", shortNames = 'e', required = true) Boolean enable
    ) {
        cnpgService.update(slug, enable);
    }

    @Command(command = "backup list", alias = "backup ls", description = "Returns postgresql backup list")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void getBackupList(
            @Option(longNames = "slug", shortNames = 's') String slug
    ) {
        cnpgService.renderBackupsTable(slug);
    }

    @Command(command = "restore", description = "Restores postgresql from chosen backup")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void restore(
            @Option(longNames = "source", shortNames = 's') String oldSlug,
            @Option(longNames = "target", shortNames = 't') String newSlug,
            @Option(longNames = "backup", shortNames = 'b') String backupName
    ) {
        cnpgService.restore(newSlug, oldSlug, backupName);
    }



}
