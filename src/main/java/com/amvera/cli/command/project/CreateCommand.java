package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ATest;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.AbstractShellComponent;

@Command(group = "Project commands")
public class CreateCommand extends AbstractShellComponent {
    private final ProjectService projectService;
    private final ProjectFlows projectFlows;
    private final ShellHelper helper;
    private final AmveraTable amveraTable;
    private final AmveraSelector selector;
    private final AmveraInput input;

    public CreateCommand(
            ProjectService projectService,
            ProjectFlows projectFlows,
            ShellHelper helper,
            AmveraTable amveraTable,
            AmveraSelector selector, AmveraInput input) {
        this.projectService = projectService;
        this.projectFlows = projectFlows;
        this.helper = helper;
        this.amveraTable = amveraTable;
        this.selector = selector;
        this.input = input;
    }

    @Command(command = "create", description = "Create new project")
    public String create(
            @Option(longNames = "config", shortNames = 'c', description = "Add configuration amvera.yml") Boolean config
    ) throws JsonProcessingException {
        String name = input.defaultInput("Название проекта: ");

        if (name == null || name.isBlank()) {
            //todo: throw exception
        }

        int tariff = selector.selectTariff();
        ATest project = projectService.createProject(name, tariff);
        String slug = project.slug();

        // add amvera.yml
        if (config) {
            // Select environment for amvera.yml
            Environment environment = selector.selectEnvironment();
            // Create amvera.yml depending on selected environment
            AmveraConfiguration configuration = projectFlows.createConfig(environment);
            projectService.addConfig(configuration, slug);
        }

        helper.println("Project created:");
        return amveraTable.singleEntityTable(project);
    }

}
