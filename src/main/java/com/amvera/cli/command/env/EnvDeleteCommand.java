package com.amvera.cli.command.env;

import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.ConfirmationInput.*;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.ArrayList;
import java.util.List;

@Command(group = "Environment variables commands")
public class EnvDeleteCommand extends AbstractShellComponent {
    private final EnvironmentService envService;
    private final ProjectService projectService;
    private final AmveraTable table;
    private final ShellHelper helper;

    public EnvDeleteCommand(EnvironmentService envService, ProjectService projectService, AmveraTable table, ShellHelper helper) {
        this.envService = envService;
        this.projectService = projectService;
        this.table = table;
        this.helper = helper;
    }

    @Command(command = "env delete", description = "Delete single or multiple environment variables")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String delete(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project,
            @Option(longNames = "id", shortNames = 'i', arityMin = 1, arityMax = 10, description = "Environments ids to delete (max 10 per command)") Integer[] ids
    ) {
        ProjectResponse p = projectService.findBy(project);
        List<Integer> idsList = ids != null ? List.of(ids) : new ArrayList<>();
        String slug = p.getSlug();
        List<EnvDTO> currentEnvs = envService.getEnvironmentBySlug(slug);
        List<EnvDTO> toDelete = currentEnvs.stream().filter(e -> idsList.contains(e.id())).toList();

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
