package com.amvera.cli.command;

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

    @Autowired
    public CreateProjectCommand(ComponentFlow.Builder componentFlowBuilder) {
        this.componentFlowBuilder = componentFlowBuilder;
    }

    @ShellMethod(value = "Create new project", group = "Create", key = "create-project", interactionMode = InteractionMode.ALL)
    public void create() {

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

        System.out.println(context.stream().toList());

        System.exit(0);
    }
}
