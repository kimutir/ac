package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "delete", group = "Delete commands")
public class DeleteCommand {
    private final ProjectService projectService;
    private final AmveraInput input;
    private final AmveraSelector selector;
    private final ShellHelper helper;

    public DeleteCommand(
            ProjectService projectService,
            AmveraInput input,
            AmveraSelector selector,
            ShellHelper helper
    ) {
        this.projectService = projectService;
        this.input = input;
        this.selector = selector;
        this.helper = helper;
    }

    @Command(command = "", description = "Delete project by id, name or slug")
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
            projectService.deleteRequest(project);
            helper.println(String.format("Project %s has been deleted", project.getSlug()));
        }
    }

}
