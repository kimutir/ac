package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.utils.Environment;
import com.amvera.cli.utils.StringToConfigParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.stereotype.Component;

@Component
public class ProjectFlows {

    private final ComponentFlow.Builder componentFlowBuilder;

    @Autowired
    public ProjectFlows(ComponentFlow.Builder componentFlowBuilder) {
        this.componentFlowBuilder = componentFlowBuilder;
    }

    public AmveraConfiguration createConfig(Environment env) throws JsonProcessingException {

        switch (env) {
            case JVM -> {
                return jvmFlow();
            }
            case PYTHON -> {
                return pythonFlow();
            }
            case NODE -> {
                return nodeFlow();
            }
            case DOCKER -> {
//                return dockerFlow();
                return null;
            }
            case DB -> {
//                return dbFlow();
                return null;
            }
        }

        // todo: throw unknown env exception
        return null;
    }

    private AmveraConfiguration pythonFlow() {
        ComponentContext<?> context = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("name")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentPython)
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("3.8")
                .and()
                .withStringInput("scriptName")
                .name("Скрипт:")
                .defaultValue("app.py")
                .and()
                .withStringInput("persistenceMount")
                .name("Дата:")
                .defaultValue("/data")
                .and()
                .withStringInput("containerPort")
                .name("Порт:")
                .defaultValue("80")
                .and()
                .withStringInput("requirementsPath")
                .name("Путь до requirements.txt:")
                .defaultValue("requirements.txt")
                .and()
                .build()
                .run().getContext();

        AmveraConfiguration config = new StringToConfigParser().parse(context);
        config.getMeta().setEnvironment("python");

        return config;
    }

    private AmveraConfiguration jvmFlow() {
        ComponentContext<?> context = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("name")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentJVM)
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("11")
                .and()
                .withStringInput("jarName")
                .name("jarName:")
                .defaultValue("main.jar")
                .and()
                .withStringInput("artifacts-key")
                .name("Artifacts key:")
                .defaultValue("build/libs/*.jar")
                .and()
                .withStringInput("artifacts-value")
                .name("Artifacts value:")
                .defaultValue("/")
                .and()
                .withStringInput("persistenceMount")
                .name("Дата:")
                .defaultValue("/data")
                .and()
                .withStringInput("containerPort")
                .name("Порт:")
                .defaultValue("80")
                .and()
                .build()
                .run()
                .getContext();

        AmveraConfiguration config = new StringToConfigParser().parse(context);
        config.getMeta().setEnvironment(Environment.JVM.name().toLowerCase());

        return config;
    }

    private AmveraConfiguration nodeFlow() {
        ComponentContext<?> context;

        ComponentContext<?> meta = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("name")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentNode)
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("18")
                .and()
                .build().run().getContext();

        String name = meta.get("name");
        String version = meta.get("version");

        if (name.equals("browser")) {
            context = componentFlowBuilder.clone().reset()
                    .withStringInput("artifacts-key")
                    .name("Artifacts key:")
                    .defaultValue("build/libs/*.jar")
                    .and()
                    .withStringInput("artifacts-value")
                    .name("Artifacts value:")
                    .defaultValue("/").and()
                    .withStringInput("additionalCommands")
                    .name("additionalCommands:")
                    .and()
                    .build().run().getContext();
        } else {
            context = componentFlowBuilder.clone().reset()
                    .withConfirmationInput("skip")
                    .name("Skip Build:")
                    .defaultValue(false)
                    .and()
                    .withStringInput("artifacts-key")
                    .name("Artifacts key:")
                    .defaultValue("*")
                    .and()
                    .withStringInput("artifacts-value")
                    .name("Artifacts value:")
                    .defaultValue("/")
                    .and()
                    .withStringInput("additionalCommands")
                    .name("additionalCommands:")
                    .and()
                    .withStringInput("scriptName")
                    .name("Script name:")
                    .defaultValue("index.js")
                    .and()
                    .withStringInput("scriptArgument")
                    .name("Script arguments:")
                    .and()
                    .withStringInput("nodeArguments")
                    .name("Node arguments:")
                    .and()
                    .withStringInput("command")
                    .name("Command:")
                    .defaultValue("npm run start")
                    .and()
                    .withStringInput("persistenceMount")
                    .name("Дата:")
                    .defaultValue("/data")
                    .and()
                    .withStringInput("containerPort")
                    .name("Порт:")
                    .defaultValue("80")
                    .and()
                    .build().run().getContext();
        }

        context.put("version", version);
        context.put("name", name);

        Boolean skip = context.get("skip");
        System.out.println("skip " + skip);

        AmveraConfiguration config = new StringToConfigParser().parse(context);
        config.getMeta().setEnvironment(Environment.NODE.name().toLowerCase());

        return config;
    }


}
