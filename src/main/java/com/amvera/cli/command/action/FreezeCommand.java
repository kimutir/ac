package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.exception.UnsupportedServiceTypeException;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraSelector;
import com.amvera.cli.utils.ProjectSelectItem;
import com.amvera.cli.utils.ServiceType;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;

@Command(command = "freeze", group = "Freeze commands")
public class FreezeCommand {

    private final ProjectService projectService;
    private final AmveraSelector selector;
    private final ShellHelper helper;

    public FreezeCommand(ProjectService projectService, AmveraSelector selector, ShellHelper helper) {
        this.projectService = projectService;
        this.selector = selector;
        this.helper = helper;
    }

    @Command(command = "", description = "Freeze project by id, name or slug")
    public void freeze(
            @Option(longNames = "id", shortNames = 'i', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String id
    ) {
        String slug;

        if (id != null) {
            ProjectGetResponse project = projectService.findBy(id);

            if (!project.getServiceType().equals(ServiceType.PROJECT)) {
                throw new UnsupportedServiceTypeException("Unable to freeze not PROJECT type");
            }

            slug = project.getSlug();
        } else {
            List<SelectorItem<ProjectSelectItem>> list = projectService.getProjects()
                    .stream()
                    .filter(p -> p.getServiceType().equals(ServiceType.PROJECT))
                    .map(ProjectGetResponse::toSelectorItem)
                    .toList();

            slug = selector.singleSelector(list, "Select project to freeze: ", true).getProject().getSlug();
        }

        ResponseEntity<Void> freezeResponse = projectService.freeze(slug);

        if (freezeResponse.getStatusCode().is2xxSuccessful()) {
            helper.print(String.format("Project %s has been frozen", slug));
        }
    }

}
