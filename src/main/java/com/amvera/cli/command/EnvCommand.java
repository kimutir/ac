package com.amvera.cli.command;

import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "env", group = "Environment variables commands")
public class EnvCommand {
    private final EnvironmentService envService;
    private final ProjectService projectService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public EnvCommand(EnvironmentService envService, ProjectService projectService, AmveraTable amveraTable, ShellHelper helper) {
        this.envService = envService;
        this.projectService = projectService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @Command(command = "", description = "Environment variables for specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void environment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.renderTable(slug);
    }

    @Command(command = "delete", alias = "env remove", description = "Environment variables for specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void deleteEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.delete(slug);
    }

    @Command(command = "add", alias = "env create", description = "Environment variables for specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void addEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.create(slug);
    }

    @Command(command = "update", alias = "env change", description = "Environment variables for specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void updateEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.update(slug);
    }
}
