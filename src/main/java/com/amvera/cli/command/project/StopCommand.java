package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class StopCommand {

    private final ProjectService projectService;

    public StopCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ShellMethod(
            key = "stop",
            value = "Stop project"
    )
    public String stop(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {
        return projectService.stop(project);
    }

}
