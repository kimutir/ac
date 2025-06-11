package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "scale", group = "Project commands")
public class ScaleCommand {

    private final ProjectService projectService;

    public ScaleCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "", description = "Change amount of project required instances")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void scale(
            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug,
            @Option(longNames = "replicas", shortNames = 'r', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Instances amount") Integer replicas
    ) {
        replicas = replicas == null ? null : Math.min(Math.abs(replicas), 5);

        projectService.scale(slug, replicas);
    }

}
