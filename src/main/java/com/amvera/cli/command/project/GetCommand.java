package com.amvera.cli.command.project;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.service.TariffService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.Tariff;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

import java.util.List;

@Command(command = "get", alias = "get", group = "Project commands")
public class GetCommand {
    private final ProjectService projectService;
    private final TariffService tariffService;
    private final EnvironmentService envService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public GetCommand(
            ProjectService projectService,
            TariffService tariffService, EnvironmentService envService,
            AmveraTable amveraTable,
            ShellHelper helper
    ) {
        this.projectService = projectService;
        this.tariffService = tariffService;
        this.envService = envService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @Command(command = "", description = "Get list of all projects including postgres and configured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String get() {
        return "all projects"; // todo: get all projects
    }

    @Command(command = "project", description = "Get list of projects")
    @CommandAvailability(provider = "userLoggedOutProvider")
    // todo: org.springframework.core.convert.ConversionFailedException
    public String project(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String project,
            @Option(longNames = "env", shortNames = 'e', description = "Returns project environments") Boolean env
    ) {
        List<ProjectGetResponse> projects = projectService.getProjects();

        if (project != null) {
            projects = projects.stream()
                    .filter(p -> String.valueOf(p.getId()).equals(project) || p.getName().equals(project) || p.getSlug().equals(project))
                    .limit(1).toList();

            if (projects.isEmpty()) return "Project was not found.";

            ProjectGetResponse singleProject = projects.getFirst();
            TariffGetResponse tariff = tariffService.getTariff(singleProject.getSlug());

            String projectTable = amveraTable.singleEntityTable(new ProjectTableModel(singleProject, Tariff.value(tariff.id())));

            if (env) {
                helper.println("PROJECT");
                helper.println(projectTable);
                helper.println("ENVIRONMENTS");
                List<EnvDTO> envs = envService.getEnvironmentBySlug(singleProject.getSlug());
                return envs.isEmpty() ? "empty" : amveraTable.environments(envs);
            }

            helper.println("PROJECT");
            return amveraTable.singleEntityTable(new ProjectTableModel(singleProject, Tariff.value(tariff.id())));
        }

        // return all found projects
        return amveraTable.projects(projects);
    }

    @Command(command = "postgresql", alias = "psql", description = "Get list of postgresql (cnpg) clusters")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String postgresql(
            // if (name, slug, id) return with envs
    ) {
        return "all postgres"; // todo: get all cnpg with envs
    }

    @Command(command = "preconfigured", alias = "conf", description = "Get list of preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String preconfigured() {
        return "all conf"; // todo: get all conf with env
    }



}
