/*
 * Copyright 2017-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amvera.cli.custom.standart.commands;

import org.jline.terminal.Terminal;
import org.springframework.shell.Utils;
import org.springframework.shell.command.CommandAlias;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.*;
import org.springframework.shell.standard.commands.Help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
public class HelpCustom extends AbstractShellComponent
        implements Help.Command {
    private static final String SPACE = "    ";
    private final Terminal terminal;

    public HelpCustom(Terminal terminal) {
        this.terminal = terminal;
    }

    @ShellMethod(
            value = "Custom Display help about available commands",
            interactionMode = InteractionMode.ALL,
            key = "help")
    public String help(
            @ShellOption(defaultValue = ShellOption.NULL,
                    valueProvider = CommandValueProvider.class,
                    value = {"-c", "--command"},
                    help = "The command на русском to obtain help for.") String command
    ) {
        if (command == null) {
            return renderCommands();
        } else {
            String commandStr = Stream.of(command)
                    .map(String::trim)
                    .collect(Collectors.joining(" "));
            return renderCommand(commandStr);
        }

    }

    private String renderCommands() {
        Map<String, CommandRegistration> registrations = Utils
                .removeHiddenCommands(getCommandCatalog().getRegistrations());

        Map<String, List<CommandRegistration>> toPrint = new HashMap<>();

        registrations.
                forEach((i, k) ->
                {
                    if (toPrint.containsKey(k.getGroup())) {
                        List<CommandRegistration> commands = toPrint.get(k.getGroup());
                        if (!commands.contains(k)) {
                            commands.add(k);
                        }
                    } else {
                        toPrint.put(k.getGroup(), new ArrayList<>(List.of(k)));
                    }
                });

        StringBuilder result = new StringBuilder();
        toPrint
                .forEach((key, value) -> {
                    // group
                    result.append(key).append("next line на русском\n");
                    // command + aliases: description
                    value.forEach(i -> {
                        StringBuilder line = new StringBuilder();
                        line.append(SPACE).append(i.getCommand());
                        String alias = i.getAliases().isEmpty() ?
                                ": " : ", " + i.getAliases().stream().map(CommandAlias::getCommand).collect(Collectors.joining(", ")) + ": ";
                        line.append(alias);
                        line.append(i.getDescription());
                        result.append(line).append("\n");
                    });
                    result.append("\n");
                });
        return result.toString();
    }

    private String renderCommand(String command) {
        Map<String, CommandRegistration> registrations = Utils
                .removeHiddenCommands(getCommandCatalog().getRegistrations());
        CommandRegistration registration = registrations.get(command);

        if (registration == null) {
            throw new IllegalArgumentException("Unknown command '" + command + "'");
        }

        StringBuilder result = new StringBuilder();
        StringBuilder name = new StringBuilder();
        StringBuilder options = new StringBuilder();
        StringBuilder aliases = new StringBuilder();

        CommandInfoModel commandInfoModel = CommandInfoModel.of(command, registration);

        name.append("\n").append("NAME").append("\n").append(SPACE).append(command).append(" - ").append(commandInfoModel.getDescription()).append("\n\n");
        options.append("OPTIONS").append("\n").append(commandInfoModel.getParameters().stream().map(i -> {
            StringBuilder option = new StringBuilder();
            option.append(SPACE).append(String.join(" or ", i.getArguments())).append(" ").append(i.getType()).append("\n");
            option.append(SPACE).append(i.getDescription()).append("\n");
            option.append(SPACE).append(i.getRequired() ? "[REQUIRED]" : "[OPTIONAL]");
            return option.toString();
        }).collect(Collectors.joining("\n\n"))).append("\n\n");


        if (!commandInfoModel.getAliases().isEmpty()) {
            aliases.append("ALSO KNOWN AS").append("\n");
            List<String> allCommands = commandInfoModel.getAliases();
            allCommands.add(registration.getCommand());
            System.out.println(allCommands);
            List<String> uniqCommands = allCommands.stream().filter(i -> !i.equals(command)).toList();
            uniqCommands.forEach(i -> {
                aliases.append(SPACE).append(i).append("\n");
            });
        }
        return result.append(name).append(options).append(aliases).toString();
    }
}
