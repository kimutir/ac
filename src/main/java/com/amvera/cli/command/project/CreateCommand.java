package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ATest;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CreateCommand extends AbstractShellComponent {
    private final ComponentFlow.Builder componentFlowBuilder;
    private final ProjectService projectService;
    private final ObjectMapper mapper;
    private final ProjectFlows projectFlows;
    private final ShellHelper helper;
    private final AmveraTable amveraTable;
    private final AmveraSelector selector;
    private final AmveraInput input;

    @Autowired
    public CreateCommand(
            ComponentFlow.Builder componentFlowBuilder,
            ProjectService projectService,
            ObjectMapper mapper,
            ProjectFlows projectFlows,
            ShellHelper helper,
            AmveraTable amveraTable,
            AmveraSelector selector, AmveraInput input) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.projectService = projectService;
        this.mapper = mapper;
        this.projectFlows = projectFlows;
        this.helper = helper;
        this.amveraTable = amveraTable;
        this.selector = selector;
        this.input = input;
    }

    @RegisterReflectionForBinding(AmveraConfiguration.class)
    @ShellMethod(
            value = "Create new project",
            key = "create",
            interactionMode = InteractionMode.ALL
    )
    public String create(
            @ShellOption(help = "Add configuration amvera.yml", value = {"--config", "-c"}) Boolean config
    ) throws JsonProcessingException {

//        ComponentFlow withoutConfiguration = componentFlowBuilder.clone().reset()
//                .withStringInput("name")
//                .name("Название проекта:")
//                .defaultValue("New project")
//                .and()
//                .build();
//
//        ComponentContext<?> context = withoutConfiguration.run().getContext();
//        int tariff = selector.selectTariff();

//        String name = context.get("name");
        String name = input.defaultInput("Название проекта: ");

        if (name == null || name.isBlank()) {
            //todo: throw exception
        }

        int tariff = selector.selectTariff();
        ATest project = projectService.addConfig(name, tariff);
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
