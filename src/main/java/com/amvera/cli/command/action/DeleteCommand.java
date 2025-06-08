package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.exception.ConfirmationException;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraInput;
import com.amvera.cli.utils.AmveraSelector;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;
import java.util.stream.Stream;

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
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String project
    ) {
        String projectSlugToDelete;

        if (project == null) {
            projectSlugToDelete = selectProjectToDelete();
        } else {
            projectSlugToDelete = projectService.findBy(project).getSlug();
        }

        String confirmPhrase = "delete forever " + projectSlugToDelete;

        boolean confirmed = input.checkedConfirmInput("Enter to delete: ", confirmPhrase);

        if (confirmed) {
            projectService.delete(projectSlugToDelete);
            helper.println(String.format("Project %s has been deleted", projectSlugToDelete));
        }
    }

    private String selectProjectToDelete() {
        List<ProjectGetResponse> projectList = projectService.getProjects();
        List<SelectorItem<String>> list = projectService.getProjects()
                .stream()
                .map(p -> {
                    String serviceType = switch (p.getServiceType()) {
                        case "compute" -> "project";
                        case "marketplace" -> "preconfigured";
                        case "cnpg" -> "postgresql";
                        default -> "unknown";
                    };

                    return SelectorItem.of(String.format("%s [ %s ]", p.getName(), serviceType), p.getSlug());
                })
                .toList();

        return selector.singleSelector(list, "Select project to delete: ", false);
    }

}
