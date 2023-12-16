package com.amvera.cli.command.info;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.exception.CustomException;
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
        throw new CustomException();
//        return "amvera version " + properties.version();
    }
}
