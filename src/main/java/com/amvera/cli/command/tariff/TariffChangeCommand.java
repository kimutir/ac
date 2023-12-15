package com.amvera.cli.command.tariff;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraSelector;
import com.amvera.cli.utils.Tariff;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TariffChangeCommand {

    private final ProjectService projectService;
    private final AmveraSelector selector;
    private final ShellHelper helper;

    public TariffChangeCommand(ProjectService projectService, AmveraSelector selector, ShellHelper helper) {
        this.projectService = projectService;
        this.selector = selector;
        this.helper = helper;
    }

    @ShellMethod(
            key = "tariff change",
            value = "Change tariff option"
    )
    public String change(@ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project) {
        ProjectResponse projectResponse = projectService.findBy(project);
        String slug = projectResponse.getSlug();

        TariffGetResponse tariffResponse = projectService.getTariff(slug);
        helper.println("Current tariff: " + Tariff.value(tariffResponse.id()));
        int tariffId = selector.selectTariff();
        projectService.changeTariff(slug, tariffId);

        return "Tariff successfully changed to: " + Tariff.value(tariffId);
    }

}
