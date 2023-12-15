package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RebuildCommand {
    private final ProjectService projectService;

    public RebuildCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ShellMethod(
            key = "rebuild",
            value = "Rebuild project"
    )
    public String rebuild(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {
        return projectService.rebuild(project);
    }

}
