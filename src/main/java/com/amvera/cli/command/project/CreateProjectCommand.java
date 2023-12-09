package com.amvera.cli.command.project;

import com.amvera.cli.utils.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.HashMap;
import java.util.Map;

@ShellComponent
public class CreateProjectCommand {

    private final ComponentFlow.Builder componentFlowBuilder;
    private final ShellHelper shellHelper;

    @Autowired
    public CreateProjectCommand(ComponentFlow.Builder componentFlowBuilder, ShellHelper shellHelper) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.shellHelper = shellHelper;
    }

    @ShellMethod(
            value = "Create new project",
            key = "create-project",
            interactionMode = InteractionMode.ALL
    )
    public String create() {

        Map<String, String> environmentComponent = new HashMap<>();
        environmentComponent.put("Python", "python");
        environmentComponent.put("Java/Kotlin", "jvm");

        Map<String, String> builderPythonComponent = new HashMap<>();
        builderPythonComponent.put("PIP", "pip");

        Map<String, String> builderJVMComponent = new HashMap<>();
        builderJVMComponent.put("Maven", "maven");
        builderJVMComponent.put("Gradle", "gradle");

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("project-name")
                .name("Название проекта:")
                .defaultValue("project-name")
                .and()
                .withSingleItemSelector("environment")
                .name("Выберите окружение:")
                .selectItems(environmentComponent)
                .next(ctx -> ctx.getResultItem().get().getItem())
                .and()
                .withSingleItemSelector("python")
                .name("Выберите инструмент:")
                .selectItems(builderPythonComponent)
                .next(ctx -> null)
                .and()
                .withSingleItemSelector("jvm")
                .name("Выберите инструмент:")
                .selectItems(builderJVMComponent)
                .next(ctx -> null)
                .and()
                .build();

        ComponentContext<?> context = flow.run().getContext();

        return context.stream().toList().toString();
    }
}
