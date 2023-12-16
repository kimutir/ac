package com.amvera.cli.command.tariff;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.service.TariffService;
import com.amvera.cli.utils.AmveraSelector;
import com.amvera.cli.utils.Tariff;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Tariff commands")
public class TariffChangeCommand {
    private final ProjectService projectService;
    private final TariffService tariffService;
    private final AmveraSelector selector;
    private final ShellHelper helper;

    public TariffChangeCommand(
            ProjectService projectService,
            TariffService tariffService,
            AmveraSelector selector,
            ShellHelper helper
    ) {
        this.tariffService = tariffService;
        this.projectService = projectService;
        this.selector = selector;
        this.helper = helper;
    }

    @Command(command = "tariff change", description = "Change tariff option")
    public String change(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        ProjectResponse projectResponse = projectService.findBy(project);
        String slug = projectResponse.getSlug();

        TariffGetResponse tariffResponse = tariffService.getTariff(slug);
        helper.println("Current tariff: " + Tariff.value(tariffResponse.id()));
        int tariffId = selector.selectTariff();
        tariffService.changeTariff(slug, tariffId);

        return "Tariff successfully changed to: " + Tariff.value(tariffId);
    }

}
