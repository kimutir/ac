package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.input.AmveraInput;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "delete", group = "Project actions commands")
public class DeleteCommand {
    private final ProjectService projectService;
    private final AmveraInput input;

    public DeleteCommand(
            ProjectService projectService,
            AmveraInput input
    ) {
        this.projectService = projectService;
        this.input = input;
    }

    @Command(command = "", description = "Deletes project by slug")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void delete(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        ProjectResponse project;

        if (slug == null) {
            project = projectService.select();
        } else {
            project = projectService.findBy(slug);
        }

        String confirmPhrase = "delete forever " + project.getSlug();

        boolean confirmed = input.checkedConfirmInput("Enter to delete: ", confirmPhrase);

        if (confirmed) {
            projectService.delete(project);
        }
    }

}
