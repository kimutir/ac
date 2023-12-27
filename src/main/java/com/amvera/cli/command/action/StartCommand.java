package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project actions commands")
public class StartCommand {
    private final ProjectService projectService;

    public StartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "start", description = "Start project")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void start(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = OptionArity.EXACTLY_ONE,
                    description = "Project slug"
            ) String slug
    ) {
        projectService.start(slug);
    }

}