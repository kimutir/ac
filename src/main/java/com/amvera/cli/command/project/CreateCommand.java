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
    private final TableCreator tableProducer;
    private final SelectorCreator selectorCreator;

    @Autowired
    public CreateCommand(
            ComponentFlow.Builder componentFlowBuilder,
            ProjectService projectService,
            ObjectMapper mapper,
            ProjectFlows projectFlows,
            ShellHelper helper, TableCreator tableProducer, SelectorCreator selectorCreator) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.projectService = projectService;
        this.mapper = mapper;
        this.projectFlows = projectFlows;
        this.helper = helper;
        this.tableProducer = tableProducer;
        this.selectorCreator = selectorCreator;
    }

    @RegisterReflectionForBinding(AmveraConfiguration.class)
    @ShellMethod(
            value = "Create new project",
            key = "create",
            interactionMode = InteractionMode.ALL
    )
    public String create(
            @ShellOption(help = "To skip configuration amvera.yml", value = {"--empty", "-e"}) Boolean empty
    ) throws JsonProcessingException {

        ComponentFlow withoutConfiguration = componentFlowBuilder.clone().reset()
                .withStringInput("name")
                .name("Название проекта:")
                .defaultValue("New project")
                .and()
                .build();

        ComponentContext<?> context = withoutConfiguration.run().getContext();
        int tariff = selectorCreator.selectTariff();

        String name = context.get("name");
        ATest project = projectService.createProject(name, tariff);
        String slug = project.slug();

        // todo: add git links
        // return table with project
        if (empty) {
            helper.print("Project created:");
            return tableProducer.singleEntytiTable(project, mapper);
        }

        // Select environment for amvera.yml
        Environment environment = selectorCreator.selectEnvironment();
        // Create amvera.yml depending on selected environment
        AmveraConfiguration configuration = projectFlows.createConfig(environment);

        return projectService.createProject(configuration, slug);
    }

}
