package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraInput;
import com.amvera.cli.utils.AmveraSelector;
import com.amvera.cli.utils.ProjectSelectItem;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;

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
            @Option(longNames = "id", shortNames = 'i', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String id
    ) {
        String projectSlugToDelete;

        if (id == null) {
            List<SelectorItem<ProjectSelectItem>> projectSelectorItemList = projectService.getProjects().stream().map(ProjectGetResponse::toSelectorItem).toList();
            projectSlugToDelete = selector.singleSelector(projectSelectorItemList, "Select project to delete: ", true).getProject().getSlug();
        } else {
            projectSlugToDelete = projectService.findBy(id).getSlug();
        }

        String confirmPhrase = "delete forever " + projectSlugToDelete;

        boolean confirmed = input.checkedConfirmInput("Enter to delete: ", confirmPhrase);

        if (confirmed) {
            projectService.deleteBySlug(projectSlugToDelete);
            helper.println(String.format("Project %s has been deleted", projectSlugToDelete));
        }
    }

}
