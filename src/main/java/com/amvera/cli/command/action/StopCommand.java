package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project actions commands")
public class StopCommand {
    private final ProjectService projectService;

    public StopCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "stop", description = "Stop project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void stop(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Project slug"
            ) String slug
    ) {
        projectService.stop(slug);
    }

}
