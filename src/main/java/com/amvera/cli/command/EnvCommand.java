package com.amvera.cli.command;

import com.amvera.cli.service.EnvironmentService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "env", alias = "env", group = "Environment variables commands")
public class EnvCommand {
    private final EnvironmentService envService;

    public EnvCommand(EnvironmentService envService) {
        this.envService = envService;
    }

    @Command(command = "", description = "Returns list of environment variables")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void environment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.renderTable(slug);
    }

    @Command(command = "delete", alias = "remove", description = "Deletes selected environment variable")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void deleteEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug,
            @Option(longNames = "id", shortNames = 'i', arity = OptionArity.EXACTLY_ONE, description = "Environment id") Long id
    ) {
        envService.delete(slug);
    }

    @Command(command = "add", alias = "create", description = "Adds environment variable to specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void addEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.create(slug);
    }

    @Command(command = "update", alias = "change", description = "Updates environment variable of specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void updateEnvironment(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.update(slug);
    }
}
