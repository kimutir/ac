package com.amvera.cli.utils;

import com.amvera.cli.dto.project.config.AmveraConfiguration;
import org.springframework.shell.component.context.ComponentContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringToConfigParser {

    private final  List<String> buildArgs = new ArrayList<>(List.of("requirementsPath", "skip", "dockerfile", "artifacts-key", "artifacts-key"));
    private final List<String> runArgs = new ArrayList<>(List.of("containerPort", "scriptName", "persistenceMount", "jarName", "command", "args"));
    private final List<String> metaArgs = new ArrayList<>(List.of("environment"));
    private final List<String> toolchainArgs = new ArrayList<>(List.of("name", "version"));

    public AmveraConfiguration parse(ComponentContext<?> context) {

        AmveraConfiguration configuration = new AmveraConfiguration();

        context.stream().forEach(i -> {
            String key = (String) i.getKey();
            Object value = i.getValue();

            if (toolchainArgs.contains(key)) {
                configuration.getMeta().getToolchain().put(key, value);
            }
            if (metaArgs.contains(key)) {
                configuration.getMeta().setEnvironment((String) value);
            }
            if (runArgs.contains(key)) {
                configuration.getRun().put(key, value);
            }
            if (buildArgs.contains(key)) {
                if (key.equals("artifacts-key")) {
                    configuration.getBuild().put("artifacts", new HashMap<>(Map.of(context.get("artifacts-key"), context.get("artifacts-value"))));
                } else {
                    configuration.getBuild().put(key, value);
                }
            }

        });

        return configuration;
    }

}
