package com.amvera.cli.command.project;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Command(group = "Project commands")
public class StopCommand {
    private final ProjectService projectService;

    public StopCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "stop", description = "Stop project")
    public String stop(
            @Option(longNames = "project", shortNames = 'p', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        return projectService.stop(project);
    }

}
