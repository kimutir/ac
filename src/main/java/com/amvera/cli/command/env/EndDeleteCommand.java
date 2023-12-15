package com.amvera.cli.command.env;

import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.ConfirmationInput.*;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class EndDeleteCommand extends AbstractShellComponent {
    private final EnvironmentService envService;
    private final ProjectService projectService;
    private final AmveraTable table;
    private final ShellHelper helper;

    public EndDeleteCommand(EnvironmentService envService, ProjectService projectService, AmveraTable table, ShellHelper helper) {
        this.envService = envService;
        this.projectService = projectService;
        this.table = table;
        this.helper = helper;
    }

    @ShellMethod(
            key = "env delete",
            value = "Delete single or multiple environment variables"
    )
    public String delete(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project,
            @ShellOption(value = {"-i", "--ids"}, arity = 10, help = "Environments ids to delete (max 10 per command)") List<Integer> ids
    ) {
        ProjectResponse p = projectService.findBy(project);
        String slug = p.getSlug();

        List<EnvDTO> currentEnvs = envService.getEnvironmentBySlug(slug);
        List<EnvDTO> toDelete = currentEnvs.stream().filter(e -> ids.contains(e.id())).toList();

        if (toDelete.isEmpty()) {
            return "Such environments do not exist.";
        } else {
            helper.println("You are going to delete: \n" + table.environments(toDelete));
        }

        ConfirmationInput confirmationInput = new ConfirmationInput(getTerminal(), "Are you sure?", false);
        confirmationInput.setResourceLoader(getResourceLoader());
        confirmationInput.setTemplateExecutor(getTemplateExecutor());
        Boolean confirmed = confirmationInput.run(ConfirmationInputContext.empty()).getResultValue();

        if (confirmed) {
            toDelete.forEach(e -> envService.deleteEnvironment(e.id(), slug));
            return "Successfully deleted!";
        }

        return "Canceled.";
    }

}
