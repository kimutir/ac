package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.config.AmveraConfiguration;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
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

    public void pythonFlow(AmveraConfiguration configuration) {
        ComponentFlow.ComponentFlowResult meta = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("instrument")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentPython)
                .postHandler(ctx -> configuration.getMeta().getToolchain().setName(ctx.get("instrument")))
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("3.8")
                .postHandler(ctx -> configuration.getMeta().getToolchain().setVersion(ctx.get("version")))
                .and()
                .build().run();

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
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
                .build().run();

        ComponentFlow.ComponentFlowResult build = componentFlowBuilder.clone().reset()
                .withStringInput("requirementsPath")
                .name("Путь до requirements.txt:")
                .defaultValue("requirements.txt")
                .and()
                .build().run();

        run.getContext().stream().forEach((i) -> configuration.getRun().put((String) i.getKey(), i.getValue()));
        build.getContext().stream().forEach((i) -> configuration.getBuild().put((String) i.getKey(), i.getValue()));
    }

    /*

     */

    public void jvmFlow(AmveraConfiguration configuration) {
        ComponentFlow.ComponentFlowResult meta = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("instrument")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentJVM)
                .postHandler(ctx -> configuration.getMeta().getToolchain().setName(ctx.get("instrument")))
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("11")
                .postHandler(ctx -> configuration.getMeta().getToolchain().setVersion(ctx.get("version")))
                .and()
                .build().run();

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withStringInput("jarName")
                .name("jarName:")
                .defaultValue("main.jar")
                .and()
                .withStringInput("persistenceMount")
                .name("Дата:")
                .defaultValue("/data")
                .and()
                .withStringInput("containerPort")
                .name("Порт:")
                .defaultValue("80")
                .and()
                .build().run();

        ComponentFlow.ComponentFlowResult build = componentFlowBuilder.clone().reset()
                .withStringInput("artifacts-key")
                .name("Artifacts key:")
                .defaultValue("build/libs/*.jar")
                .and()
                .withStringInput("artifacts-value")
                .name("Artifacts value:")
                .defaultValue("/")
                .and()
                .build().run();

        run.getContext().stream().forEach((i) -> configuration.getRun().put((String) i.getKey(), i.getValue()));
        String key = build.getContext().get("artifacts-key");
        String value = build.getContext().get("artifacts-value");

        configuration.getBuild().put(key, value);
    }

    public void nodeFlow(AmveraConfiguration configuration) {
        ComponentFlow.ComponentFlowResult meta = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("instrument")
                .name("Выберите инструмент:")
                .selectItems(ProjectComponents.instrumentNode)
                .postHandler(ctx -> configuration.getMeta().getToolchain().setName(ctx.get("instrument")))
                .and()
                .withStringInput("version")
                .name("Версия:")
                .defaultValue("18")
                .postHandler(ctx -> configuration.getMeta().getToolchain().setVersion(ctx.get("version")))
                .and()
                .build().run();

        if (meta.getContext().get("instrument").equals("browser")) {
            ComponentFlow.ComponentFlowResult buildBrowser = componentFlowBuilder.clone().reset()
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
                    .build().run();

            ComponentContext<?> context = buildBrowser.getContext();

            String key = context.get("artifacts-key");
            String value = context.get("artifacts-value");

//            if (context.containsKey("additionalCommands")) {
//                System.out.println("contains");
////                configuration.getBuild().put("additionalCommands", ((String) context.get("additionalCommands")).trim());
//            } else {
//                System.out.println("doesnt");
//            }
            System.out.println(context.stream().toList());
            Object o = context.get("additionalCommands");
            configuration.getBuild().put(key.trim(), value.trim());
        }

//        ComponentFlow.ComponentFlowResult runNPM = componentFlowBuilder.clone().reset()
//                .withStringInput("jarName")
//                .name("jarName:")
//                .defaultValue("main.jar")
//                .and()
//                .withStringInput("persistenceMount")
//                .name("Дата:")
//                .defaultValue("/data")
//                .and()
//                .withStringInput("containerPort")
//                .name("Порт:")
//                .defaultValue("80")
//                .and()
//                .build().run();
//
//
//
//        run.getContext().stream().forEach((i) -> configuration.getRun().put((String) i.getKey(), i.getValue()));

    }


}
