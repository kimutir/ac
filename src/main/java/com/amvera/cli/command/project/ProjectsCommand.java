package com.amvera.cli.command.project;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.model.ProjectFullInfo;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.ProjectTariff;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.TableCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.shell.table.CellMatchers.at;

@ShellComponent
public class ProjectsCommand {
    private final ProjectService projectService;
    private final TableCreator tableCreator;
    private final ObjectMapper mapper;
    private final ShellHelper helper;

    @Autowired
    public ProjectsCommand(ProjectService projectService, TableCreator tableCreator, ObjectMapper mapper, ShellHelper helper) {
        this.projectService = projectService;
        this.tableCreator = tableCreator;
        this.mapper = mapper;
        this.helper = helper;
    }

    @ShellMethod(
            value = "Get list of projects",
            key = "project",
            interactionMode = InteractionMode.ALL
    )
    // todo: org.springframework.core.convert.ConversionFailedException
    public String project(
            @ShellOption(
                    defaultValue = ShellOption.NULL,
                    arity = 1,
                    help = "Project id, name or slug",
                    value = {"", "-p", "--project"}) String project
    ) {
        List<ProjectResponse> projects = projectService.getProjects();

        if (project != null) {
            projects = projects.stream()
                    .filter(p -> String.valueOf(p.getId()).equals(project) || p.getName().equals(project) || p.getSlug().equals(project))
                    .limit(1).toList();

            if (projects.isEmpty()) return "Project was not found.";

            ProjectResponse singleProject = projects.getFirst();
            TariffGetResponse tariff = projectService.getTariff(singleProject.getSlug());

            return tableCreator.singleEntytiTable(new ProjectFullInfo(singleProject, ProjectTariff.value(tariff.id())), mapper);
        }


        return allProjectsTable(projects);
    }

    /**
     * Returns table of all user projects.
     *
     * @param projects List<ProjectResponse>
     * @return String
     */
    private String allProjectsTable(List<ProjectResponse> projects) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("name", "TITLE");
        headers.put("status", "Status");
        headers.put("requiredInstances", "REQ INST");
        headers.put("instances", "CUR INST");
        TableModel model = new BeanListTableModel<>(projects, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < projects.size() + 1; i++) {
            tableBuilder.on(at(i, 0)).addFormatter(new CustomFormatter());
            tableBuilder.on(at(i, 1)).addFormatter(new CustomFormatter());
            tableBuilder.on(at(i, 2)).addFormatter(new CustomFormatter());
            tableBuilder.on(at(i, 3)).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 4)).addAligner(SimpleHorizontalAligner.center);
        }

        return tableBuilder.build().render(180);
    }

    /**
     * Custom formatter for table cells to add some spaces.
     */
    private static class CustomFormatter implements Formatter {
        @Override
        public String[] format(Object value) {
            String[] result = new String[1];
            result[0] = value + "  ";
            return result;
        }
    }
}
