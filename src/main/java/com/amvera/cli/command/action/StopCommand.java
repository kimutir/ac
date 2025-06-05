package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project commands")
public class StopCommand {
    private final ProjectService projectService;

    public StopCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "stop", description = "Stop project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String stop(
            @Option(longNames = "project", shortNames = 'p', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.stop(project);
    }

}
