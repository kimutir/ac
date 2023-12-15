package com.amvera.cli.command.project;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.Tariff;
import com.amvera.cli.utils.ShellHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class ProjectCommand {
    private final ProjectService projectService;
    private final EnvironmentService envService;
    private final AmveraTable amveraTable;
    private final ObjectMapper mapper;
    private final ShellHelper helper;

    @Autowired
    public ProjectCommand(
            ProjectService projectService,
            EnvironmentService envService,
            AmveraTable amveraTable,
            ObjectMapper mapper,
            ShellHelper helper) {
        this.projectService = projectService;
        this.envService = envService;
        this.amveraTable = amveraTable;
        this.mapper = mapper;
        this.helper = helper;
    }

    @ShellMethod(
            value = "Get list of projects or single project",
            key = "project",
            interactionMode = InteractionMode.ALL
    )
    // todo: org.springframework.core.convert.ConversionFailedException
    public String project(
            @ShellOption(
                    defaultValue = ShellOption.NULL,
                    arity = 1,
                    help = "Project id, name or slug",
                    value = {"-p", "--project"}) String project,
            @ShellOption(value = {"-e", "--env"}, help = "Returns project environments") Boolean env
    ) {
        List<ProjectResponse> projects = projectService.getProjects();

        if (project != null) {
            projects = projects.stream()
                    .filter(p -> String.valueOf(p.getId()).equals(project) || p.getName().equals(project) || p.getSlug().equals(project))
                    .limit(1).toList();

            if (projects.isEmpty()) return "Project was not found.";

            ProjectResponse singleProject = projects.getFirst();
            TariffGetResponse tariff = projectService.getTariff(singleProject.getSlug());

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





}
