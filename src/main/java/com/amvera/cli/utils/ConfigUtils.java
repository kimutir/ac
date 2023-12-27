package com.amvera.cli.utils;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.github.GithubRelease;
import com.amvera.cli.dto.user.UserConfig;
import com.amvera.cli.exception.InformException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

@Component
public class ConfigUtils {

    private final AppProperties properties;
    private final ObjectMapper mapper;
    private final ShellHelper helper;
    private final ComponentFlow.Builder componentFlowBuilder;

    public ConfigUtils(
            AppProperties properties,
            ObjectMapper mapper, ShellHelper helper, ComponentFlow.Builder componentFlowBuilder
    ) {
        this.properties = properties;
        this.mapper = mapper;
        this.helper = helper;
        this.componentFlowBuilder = componentFlowBuilder;
    }

    public boolean shouldUpdate() {
        UserConfig userConfig = readUserConfig();

        if (userConfig == null || !userConfig.autoUpdateEnabled()) {
            return false;
        }

        if (Duration.between(OffsetDateTime.now(), userConfig.lastUpdateCheckedAt()).abs().toMinutes() > 1440) {
            updateUserConfig(true, OffsetDateTime.now());

            String currentVersion = properties.version();
            String lastVersion = getLatestVersion();

            return !Objects.equals(currentVersion, lastVersion);
        }

        return false;
    }

    public void update() {
        String lastVersion = getLatestVersion();
        String currentVersion = properties.version();

        ComponentContext<?> context = componentFlowBuilder.clone().reset()
                .withConfirmationInput("agree")
                .defaultValue(false)
                .name(String.format("New version detected. \n Current version: %s \n New version: %s \n Would you like to update amvera-cli?", currentVersion, lastVersion))
                .and()
                .build()
                .run().getContext();

        boolean agree = context.get("agree");

        if (!agree) return;

        String installCommand = "curl -sSL https://raw.githubusercontent.com/amvera-cloud/cli/master/amvera-install.sh | bash -s " + lastVersion;

        try {
            String os = System.getProperty("os.name").toLowerCase();

            ProcessBuilder builder;

            if (os.contains("win")) {
                builder = new ProcessBuilder("powershell.exe", "-Command", installCommand);
            } else {
                builder = new ProcessBuilder("bash", "-c", installCommand);
            }

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                helper.println(line);
            }

            helper.println("Updating completed");
            System.exit(0);
        } catch (Exception e) {
            helper.printError(String.format("Failed to update: %s", e.getMessage()));
            System.exit(1);
        }
    }

    public UserConfig createUserConfig() {
        var userConfig = new UserConfig(true, OffsetDateTime.now());
        try {
            if (Files.notExists(FileUtils.AMVERA_DIR)) {
                Files.createDirectories(FileUtils.AMVERA_DIR);
            }

            mapper.writeValue(FileUtils.CONFIG_PATH.toFile(), userConfig);

            return userConfig;
        } catch (IOException e) {
            throw new InformException("Unable to save config. Contact us to solve the problem.");
        }
    }

    public UserConfig updateUserConfig(boolean enabled, OffsetDateTime time) {
        try {
            var userConfig = new UserConfig(enabled, time);

            if (Files.notExists(FileUtils.AMVERA_DIR)) {
                Files.createDirectories(FileUtils.AMVERA_DIR);
            }

            mapper.writeValue(FileUtils.CONFIG_PATH.toFile(), userConfig);

            return userConfig;
        } catch (IOException e) {
            throw new InformException("Unable to save config. Contact us to solve the problem.");
        }
    }

    public UserConfig readUserConfig() {
        try {
            return mapper.readValue(FileUtils.CONFIG_PATH.toFile(), UserConfig.class);
        } catch (IOException e) {
            return null;
        }
    }

    public String getLatestVersion() {
        GithubRelease release;

        try {
            release = client().get().retrieve().body(GithubRelease.class);
        } catch (HttpClientErrorException e) {
            release = null;
        }

        if (release == null || release.draft() || release.prerelease()) return properties.version();

        return release.tagName();
    }

    private RestClient client() {
        return RestClient.create("https://api.github.com/repos/amvera-cloud/cli/releases/latest");
    }

}
