package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project commands")
public class RestartCommand {
    private final ProjectService projectService;

    public RestartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "restart", description = "Restart project")
    public String restart(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.restart(project);
    }

}
