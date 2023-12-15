package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraInput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class DeleteCommand {

    private final ProjectService projectService;
    private final AmveraInput input;

    public DeleteCommand(ProjectService projectService, AmveraInput input) {
        this.projectService = projectService;
        this.input = input;
    }

    @ShellMethod(
            key = "delete",
            value = "Delete project"
    )
    public String delete(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {

        ProjectResponse projectResponse = projectService.findBy(project);
        String name = projectResponse.getName();

        String confirmPhrase = "удалить навсегда " + name;

        String phrase = input.confirmInput("Enter to delete: ", confirmPhrase);

        if (phrase == null || phrase.isBlank()) {
            // todo: throw exception
        }

        assert phrase != null;
        if (phrase.trim().equals(confirmPhrase)) {
            return projectService.delete(project);
        }

        return null;
    }

}
