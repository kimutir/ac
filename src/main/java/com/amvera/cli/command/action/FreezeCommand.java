package com.amvera.cli.command.action;

import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(command = "freeze", group = "Freeze commands")
public class FreezeCommand {

    private final ProjectService projectService;

    public FreezeCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Command(command = "", description = "Freeze project by id, name or slug")
    public void freeze(
            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        projectService.freeze(slug);
    }

}
