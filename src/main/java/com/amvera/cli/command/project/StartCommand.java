package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Project commands")
public class StartCommand {
    private final ProjectService projectService;

    public StartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "start", description = "Start project")
    public String start(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.start(project);
    }

}