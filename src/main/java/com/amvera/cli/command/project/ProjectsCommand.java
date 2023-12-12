package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
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

    public ProjectsCommand(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ShellMethod(
            value = "Get list of projects",
            key = "projects",
            interactionMode = InteractionMode.ALL
    )
    public String projects(
            @ShellOption(defaultValue = ShellOption.NULL, value = {"--status", "-s"}, help = "Project status") String status
    ) {
        List<ProjectResponse> projects = projectService.getProjects();

        if (status != null && !status.equals(ShellOption.NULL) && !status.isBlank()) {
            projects = projects.stream().filter(p -> p.getStatus().equalsIgnoreCase(status)).toList();
        }

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
     * Add space after string
     */
    static class CustomFormatter implements Formatter {
        @Override
        public String[] format(Object value) {
            String[] result = new String[1];
            result[0] = value + "  ";
            return result;
        }
    }
}
