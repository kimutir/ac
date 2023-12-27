package com.amvera.cli.command;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.utils.ConfigUtils;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.annotation.Command;

import java.util.Objects;

@Command(command = "update", group = "Built-In Commands")
public class UpdateCommand {

    private final ConfigUtils configUtils;
    private final ShellHelper helper;
    private final AppProperties properties;

    public UpdateCommand(
            ConfigUtils configUtils,
            ShellHelper helper,
            AppProperties properties
    ) {
        this.configUtils = configUtils;
        this.helper = helper;
        this.properties = properties;
    }

    @Command(command = "", description = "Updates to latest version")
    public void update() {
        String latestVersion = configUtils.getLatestVersion();
        String currentVersion = properties.version();

        if (Objects.equals(latestVersion, currentVersion)) {
            helper.println("Current version is up to date.");
        } else {
            configUtils.update();
        }
    }

}
