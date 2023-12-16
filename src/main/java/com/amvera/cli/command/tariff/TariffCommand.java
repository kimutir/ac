package com.amvera.cli.command.tariff;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.model.TariffTableModel;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.service.TariffService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Tariff commands")
public class TariffCommand {
    private final ProjectService projectService;
    private final TariffService tariffService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public TariffCommand(
            ProjectService projectService,
            TariffService tariffService,
            AmveraTable amveraTable,
            ShellHelper helper
    ) {
        this.projectService = projectService;
        this.tariffService = tariffService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @Command(command = "tariff", description = "Project tariff information")
    public String tariff(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        ProjectResponse projectResponse = projectService.findBy(project);
        String slug = projectResponse.getSlug();
        TariffGetResponse tariff = tariffService.getTariff(slug);
        helper.println("TARIFF");
        return amveraTable.singleEntityTable(new TariffTableModel(tariff));
    }

}
