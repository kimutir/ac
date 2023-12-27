package com.amvera.cli.command;

import com.amvera.cli.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
@Command(group = "Built-In Commands")
public class VersionCommand {
    private final AppProperties properties;
    @Autowired
    public VersionCommand(AppProperties properties) {
        this.properties = properties;
    }

    @Command(command = "version", alias = {"--version", "-v"}, description = "Cli application version")
    public String version() {
        return "amvera-cli version " + properties.version();
    }
}
