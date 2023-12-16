package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraInput;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Command(group = "Project commands")
public class DeleteCommand {

    private final ProjectService projectService;
    private final AmveraInput input;

    public DeleteCommand(ProjectService projectService, AmveraInput input) {
        this.projectService = projectService;
        this.input = input;
    }

    @Command(command = "delete", description = "Delete project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String delete(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        ProjectResponse projectResponse = projectService.findBy(project);
        String name = projectResponse.getName();

        String confirmPhrase = "удалить навсегда " + name;

        String phrase = input.confirmInput("Enter to delete: ", confirmPhrase);

        if (phrase == null || phrase.isBlank()) {
            // todo: throw exception and exit
        }

        assert phrase != null;
        if (phrase.trim().equals(confirmPhrase)) {
            return projectService.delete(project);
        }

        return null;
    }

}
