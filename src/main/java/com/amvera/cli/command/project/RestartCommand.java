package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RestartCommand {

    private final ProjectService projectService;

    public RestartCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ShellMethod(
            key = "restart",
            value = "Restart project"
    )
    public String restart(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {
        return projectService.restart(project);
    }

}
