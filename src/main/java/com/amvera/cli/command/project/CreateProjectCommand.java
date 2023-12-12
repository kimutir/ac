package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ATest;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.ProjectTariff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CreateProjectCommand {
    private final ComponentFlow.Builder componentFlowBuilder;
    private final ProjectService projectService;
    private final ObjectMapper mapper;
    private final ProjectFlows projectFlows;

    @Autowired
    public CreateProjectCommand(ComponentFlow.Builder componentFlowBuilder, ProjectService projectService, ObjectMapper mapper, ProjectFlows projectFlows) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.projectService = projectService;
        this.mapper = mapper;
        this.projectFlows = projectFlows;
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

        final AmveraConfiguration configuration = new AmveraConfiguration();

        ComponentFlow withoutConfiguration = componentFlowBuilder.clone().reset()
                .withStringInput("name")
                .name("Название проекта:")
                .defaultValue("New project")
                .and()
                .withSingleItemSelector("tariff")
                // todo: make insertion order by overriding LinkedHashMap
                .name("Выберите тариф:")
                .selectItems(ProjectComponents.tariff)
                .and().build();

        ComponentContext<?> context = withoutConfiguration.run().getContext();

        Integer tariff = ProjectTariff.value(context.get("tariff"));
        String name = context.get("name");
        ATest project = projectService.createProject(name, tariff);
        String slug = project.slug();

        if (empty) {
            return project.name() + " Project created";
        }

        ComponentFlow withConfiguration = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("environment")
                .name("Выберите окружение:")
                .selectItems(ProjectComponents.environment)
                .postHandler(ctx -> {
                    configuration.getMeta().setEnvironment(ctx.get("environment"));
                    switch ((String) ctx.get("environment")) {
                        case "python" -> projectFlows.pythonFlow(configuration);
                        case "jvm" -> projectFlows.jvmFlow(configuration);
                        case "node" -> projectFlows.nodeFlow(configuration);
//                        case "node" -> projectFlows.jvmFlow(configuration);
                    }

                })
                .and()
                .build();

        withConfiguration.run();

        String s = mapper.writeValueAsString(configuration);
        System.out.println(configuration.getBuild());
        projectService.createProject(configuration, slug);
        return s;
    }


}
