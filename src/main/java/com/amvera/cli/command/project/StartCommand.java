package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class StartCommand {
    private final ProjectService projectService;

    public StartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ShellMethod(
            key = "start",
            value = "Start project"
    )
    public String start(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {
        return projectService.start(project);
    }

}
