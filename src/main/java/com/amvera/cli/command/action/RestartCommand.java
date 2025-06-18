package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "restart", group = "Project actions commands")
public class RestartCommand {
    private final ProjectService projectService;

    public RestartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "", description = "Restart project")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void restart(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        projectService.restart(slug);
    }

}
