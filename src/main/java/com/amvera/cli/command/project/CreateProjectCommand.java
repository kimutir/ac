package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.ATest;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ShellComponent
public class CreateProjectCommand extends AbstractShellComponent {
    private final ComponentFlow.Builder componentFlowBuilder;
    private final ProjectService projectService;
    private final ObjectMapper mapper;
    private final ProjectFlows projectFlows;
    private final ShellHelper helper;

    @Autowired
    public CreateProjectCommand(
            ComponentFlow.Builder componentFlowBuilder,
            ProjectService projectService,
            ObjectMapper mapper,
            ProjectFlows projectFlows,
            ShellHelper helper) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.projectService = projectService;
        this.mapper = mapper;
        this.projectFlows = projectFlows;
        this.helper = helper;
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
        int tariff = selectTariff();

        String name = context.get("name");
        ATest project = projectService.createProject(name, tariff);
        String slug = project.slug();

        if (empty) {
            SingleEntityTableModelBuilder tableModelBuilder = new SingleEntityTableModelBuilder(project, mapper);
            TableModel model = tableModelBuilder.build();
            TableBuilder tableBuilder = new TableBuilder(model);
            Table table = tableBuilder
                    .addInnerBorder(BorderStyle.oldschool)
                    .addHeaderBorder(BorderStyle.fancy_light)
                    .on(CellMatchers.column(0)).addFormatter(value -> new String[]{value + "  "})
                    .on(CellMatchers.column(1)).addFormatter(value -> new String[]{value + "  "})
                    .build();

            helper.print("Project created:");

            return table.render(100);
        }

        // Select environment for amvera.yml
        Environment environment = selectEnvironment();
        helper.print("selected env" + environment.name());
        // Create amvera.yml depending on selected environment
        AmveraConfiguration configuration = projectFlows.createConfig(environment);

        projectService.createProject(configuration, slug);

        return null;
    }

    private Environment selectEnvironment() {
        List<SelectorItem<String>> items = Arrays.asList(
                SelectorItem.of("Python", Environment.PYTHON.name()),
                SelectorItem.of("Java/Kotlin", Environment.JVM.name()),
                SelectorItem.of("Node", Environment.NODE.name()),
                SelectorItem.of("Docker", Environment.DOCKER.name()),
                SelectorItem.of("DB", Environment.DB.name())
        );
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                items, "Environment", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());

        return Environment.valueOf(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private int selectTariff() {
        List<SelectorItem<String>> items = Arrays.asList(
                SelectorItem.of("Пробный", ProjectTariff.TRY.title()),
                SelectorItem.of("Начальный", ProjectTariff.BEGINNER.title()),
                SelectorItem.of("Начальный Плюс", ProjectTariff.BEGINNER_PLUS.title()),
                SelectorItem.of("Стандартный", ProjectTariff.STANDARD.title()),
                SelectorItem.of("Ультра", ProjectTariff.ULTRA.title())
        );

        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                items, "Select tariff", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());

        return ProjectTariff.value(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }


}
