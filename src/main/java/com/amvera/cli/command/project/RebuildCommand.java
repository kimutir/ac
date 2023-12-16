package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellMethod;

@Command(group = "Project commands")
public class RebuildCommand {
    private final ProjectService projectService;

    public RebuildCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "rebuild", description = "Rebuild project")
    public String rebuild(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.rebuild(project);
    }

}
