package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ProjectPostResponse;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
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
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String create(
            @Option(longNames = "config", shortNames = 'c', description = "Add configuration amvera.yml") Boolean config
    ) throws JsonProcessingException {

        String name = input.defaultInput("Название проекта: ");

        if (name == null || name.isBlank()) {
            throw new EmptyValueException("Project name can not be empty.");
        }

        int tariff = selector.selectTariff();
        ProjectPostResponse project = projectService.createProject(name, tariff);
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
//        return amveraTable.singleEntityTable(project);

        return amveraTable.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff)));
    }

}

/*
{
    "id": 11462,
    "ownerId": "6df2158f-c101-42a5-9793-b0d9829b7564",
    "ownerName": "kimutir",
    "name": "test",
    "slug": "test",
    "serviceType": "compute",
    "status": "EMPTY",
    "statusMessage": "",
    "requiredInstances": 1,
    "instances": 0,
    "active": true,
    "created": 1702802626.867364000,
    "deactivated": null
}

//создание
{
    "username": "kimutir",
    "name": "test2",
    "slug": "test2",
    "ready": false,
    "instances": 0,
    "requiredInstances": 1,
    "buildStatus": "CREATING",
    "buildStatusMessage": ""
}
 */