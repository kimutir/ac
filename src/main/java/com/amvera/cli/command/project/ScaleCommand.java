package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project commands")
public class ScaleCommand {

    private final ProjectService projectService;

    public ScaleCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "scale", description = "Change amount of project required instances")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String scale(
            @Option(longNames = "project", shortNames = 'p', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project,
            @Option(longNames = "instance", shortNames = 'i', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) Integer instance
    ) {

        if (instance < 0) instance = 0;
        if (instance > 10) instance = 10;

        return projectService.scale(project, instance);
    }

}
