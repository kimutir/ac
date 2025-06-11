package com.amvera.cli.command;

import com.amvera.cli.service.ProjectService;
import com.amvera.cli.service.TariffService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "tariff", group = "Tariff commands")
public class TariffCommand {
    private final ProjectService projectService;
    private final TariffService tariffService;

    public TariffCommand(
            TariffService tariffService,
            ProjectService projectService
    ) {
        this.projectService = projectService;
        this.tariffService = tariffService;
    }

    @Command(command = "", description = "Project tariff information")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void get(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        projectService.renderTariffTable(slug);
    }

    @Command(command = "update", description = "Change tariff option")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void update(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        projectService.updateTariff(slug);
    }

    @Command(command = "list", alias = "tariff ls", description = "Project tariff information")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void list() {
        tariffService.renderTable();
    }

}
