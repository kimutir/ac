package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project commands")
public class RebuildCommand {
    private final ProjectService projectService;

    public RebuildCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "rebuild", description = "Rebuild project only! Does not work for postgresql and preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String rebuild(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.rebuild(project);
    }

}
