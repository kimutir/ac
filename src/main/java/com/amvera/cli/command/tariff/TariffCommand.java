package com.amvera.cli.command.tariff;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.model.TariffTableModel;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TariffCommand {
    private final ProjectService projectService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public TariffCommand(ProjectService projectService, AmveraTable amveraTable, ShellHelper helper) {
        this.projectService = projectService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @ShellMethod(
            key = "tariff",
            value = "Project tariff information"
    )
    public String tariff(@ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project) {
        ProjectResponse projectResponse = projectService.findBy(project);
        String slug = projectResponse.getSlug();
        TariffGetResponse tariff = projectService.getTariff(slug);
        helper.println("TARIFF");
        return amveraTable.singleEntityTable(new TariffTableModel(tariff));
    }

}
