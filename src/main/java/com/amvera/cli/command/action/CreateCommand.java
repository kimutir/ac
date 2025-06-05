package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.EnvPostRequest;
import com.amvera.cli.dto.project.ProjectPostResponse;
import com.amvera.cli.dto.project.config.*;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.service.MarketplaceService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.util.StringUtils;

import java.util.*;

@Command(command = "create", alias = "create", group = "Create commands")
public class CreateCommand extends AbstractShellComponent {
    private final ProjectService projectService;
    private final MarketplaceService marketplaceService;
    private final ShellHelper helper;
    private final AmveraTable amveraTable;
    private final AmveraSelector selector;
    private final AmveraInput input;

    private static final String MARKETPLACE_VERSION = "1";

    public CreateCommand(
            ProjectService projectService,
            MarketplaceService marketplaceService,
            ShellHelper helper,
            AmveraTable amveraTable,
            AmveraSelector selector, AmveraInput input) {
        this.projectService = projectService;
        this.marketplaceService = marketplaceService;
        this.helper = helper;
        this.amveraTable = amveraTable;
        this.selector = selector;
        this.input = input;
    }

    @Command(command = "", description = "todo: add description")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String create() {
        int serviceTypeId = selector.selectServiceType();
        String name = input.notBlankOrNullInput("Enter project name: ");
        int tariff = selector.selectTariff();
        boolean addConfig;

        addConfig = ServiceType.valueOf(serviceTypeId) == ServiceType.PROJECT
                ? selector.yesOrNoSingleSelector("Would you like to add configuration?") : false;

//        ConfigGetResponse config = switch (ServiceType.valueOf(serviceTypeId)) {
//            case PROJECT -> addConfig ? projectService.getConfig() : null;
//            case POSTGRESQL -> null;
//            case PRECONFIGURED -> marketplaceService.getMarketplaceConfig();
//            case null -> null;
//        };

        MarketplaceConfigGetResponse config = marketplaceService.getMarketplaceConfig();

        if (config != null) {
            marketConfig(config);
        }


        return "todo: create command"; // todo: add create command logic
    }

    @Command(command = "project", description = "Create new project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String project(
            @Option(longNames = "config", shortNames = 'c', description = "Add configuration amvera.yml") Boolean config
    ) throws JsonProcessingException {
        String name = input.defaultInput("Project name: ");

        if (name == null || name.isBlank()) {
//            throw new EmptyValueException("Project name can not be empty.");
        }

        int tariff = selector.selectTariff();
        ProjectPostResponse project = projectService.createProject(name, tariff);
        String slug = project.slug();

        // add amvera.yml
//        if (config) {
//            AmveraConfiguration configuration = createConfiguration(projectService.getConfig());
//            projectService.addConfig(configuration, slug);
//        }

        helper.println("Project created:");

        return amveraTable.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff)));
    }

    @Command(command = "postgresql", alias = "psql", description = "Create postgres (cnpg) cluster")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String postgresql() {
        return "todo: add cnpg creation logic"; // todo: add cnpg creation logic
    }

    @Command(command = "preconfigured", alias = "conf", description = "Create preconfigured service from marketplace")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String preconfigured() {
        return "todo: add preconfigured creation logic"; // todo: add preconfigured creation logic
    }

    private AmveraConfiguration yamlConfig() {
        AmveraConfiguration config = new AmveraConfiguration();
        return config;
    }

    private MarketplaceConfigPostRequest marketConfig(MarketplaceConfigGetResponse params) {
        MarketplaceConfigPostRequest config = new MarketplaceConfigPostRequest();

        List<SelectorItem<String>> serviceTypes = params.availableParameters()
                .keySet().stream().map(i -> SelectorItem.of(i, i)).toList();

        String selectedServiceType = selector.singleSelector(serviceTypes, "Service type: ");

        List<SelectorItem<String>> service = params.availableParameters().get(selectedServiceType)
                .keySet().stream().map(i -> SelectorItem.of(i, i)).toList();

        String selectedService = selector.singleSelector(service, "Service: ");

        Map<String, DefaultConfValuesGetResponse> metaSection = params.availableParameters()
                .get(selectedServiceType).get(selectedService).get(MARKETPLACE_VERSION).get("meta");

        if (metaSection != null) {
            Meta meta = new Meta();
            Map<String, Object> metaToolchainMap = new HashMap<>();
            meta.setEnvironment(selectedServiceType);

            helper.println(toSectionTitle("meta"));
            String version = input.notBlankOrNullInput("Version: ", metaSection.get("version").defaultValue());
            metaToolchainMap.put("version", version);
            metaToolchainMap.put("name", selectedService);

            meta.setToolchain(metaToolchainMap);
            config.setMeta(meta);
        } else {
            config.setMeta(new Meta());
        }

        Map<String, DefaultConfValuesGetResponse> runSection = params.availableParameters()
                .get(selectedServiceType).get(selectedService).get(MARKETPLACE_VERSION).get("run");

        if (runSection != null) {
            helper.println(toSectionTitle("run"));
            Map<String, Object> runMap = new HashMap<>();
            String runArgs = input.inputWithDefault("Args: ", runSection.get("args").defaultValue());

            runMap.put("args", runArgs.isBlank() ? null : runArgs);
            config.setRun(runMap);
        } else  {
            config.setRun(new HashMap<>());
        }

        Map<String, DefaultConfValuesGetResponse> envsSection = params.availableParameters()
                .get(selectedServiceType).get(selectedService).get(MARKETPLACE_VERSION).get("envvars");

        if (envsSection != null) {
            helper.println(toSectionTitle("envs"));
            List<EnvPostRequest> envs = new ArrayList<>();
            boolean isSecret;

            for (Map.Entry<String, DefaultConfValuesGetResponse> entry : envsSection.entrySet()) {
                String secretKey = entry.getKey();
                DefaultConfValuesGetResponse secretValue = entry.getValue();
                String value = input.notBlankOrNullInput(String.format("%s: ", secretKey), secretValue.defaultValue());
                isSecret = secretValue.type().equals("Secret");
                envs.add(new EnvPostRequest(secretKey, value, isSecret));
            }

            config.setEnvvars(envs);
        } else {
            config.setEnvvars(new ArrayList<>());
        }

        return config;
    }

    private AmveraConfiguration createConfiguration(Map<String, Map<String, Map<String, Map<String, DefaultConfValuesGetResponse>>>> params) {
        AmveraConfiguration configuration = new AmveraConfiguration();

        // meta section
        helper.println(toSectionTitle("meta"));

        List<SelectorItem<String>> environments = params.keySet().stream().map(i -> SelectorItem.of(i, i)).toList();
        String selectedEnvironment = selector.singleSelector(environments, "Environment: ");
        List<SelectorItem<String>> instruments = params.get(selectedEnvironment).keySet().stream().map(i -> SelectorItem.of(i, i)).toList();
        String selectedInstrument = selector.singleSelector(instruments, "Instrument: ");

        Map<String, DefaultConfValuesGetResponse> metaMap = params.get(selectedEnvironment).get(selectedInstrument).get("meta");
        Meta meta = configMetaSection(configuration, metaMap, selectedEnvironment, selectedInstrument);

        // build section
        Map<String, DefaultConfValuesGetResponse> buildMap = params.get(selectedEnvironment).get(selectedInstrument).get("build");
        configBuildSection(configuration, buildMap);

        // run section
        Map<String, DefaultConfValuesGetResponse> runMap = params.get(selectedEnvironment).get(selectedInstrument).get("run");
        configRunSection(configuration, runMap);

        configuration.setMeta(meta);

        return configuration;
    }

    private void configRunSection(AmveraConfiguration config, Map<String, DefaultConfValuesGetResponse> map) {
        if (map == null) return;
        helper.println(toSectionTitle("run"));
        map.forEach((k, v) -> {
            if (v == null) return;
            String inputValue = input.inputWithDefault(toTitle(k), v.defaultValue());
            if (inputValue == null || inputValue.isBlank()) return;
            config.getRun().put(k, inputValue);
        });
    }

    private void configBuildSection(AmveraConfiguration config, Map<String, DefaultConfValuesGetResponse> map) {
        if (map == null) return;
        helper.println(toSectionTitle("build"));
        map.forEach((k, v) -> {
            if (v == null) return;
            if (k.equals("artifacts")) {
                List<String> artifacts = Arrays.stream(v.defaultValue().split(":")).toList();
                String artifactKey = input.inputWithDefault(toTitle("artifacts-key"), artifacts.get(0).trim());
                String artifactValue = input.inputWithDefault(toTitle("artifacts-value"), artifacts.get(1).trim());
                config.getBuild().put("artifacts", new HashMap<>(Map.of(artifactKey, artifactValue)));
                return;
            }
            String inputValue = input.inputWithDefault(toTitle(k), v.defaultValue());
            if (inputValue == null || inputValue.isBlank()) return;
            config.getBuild().put(k, inputValue);
        });
    }

    private Meta configMetaSection(AmveraConfiguration config, Map<String, DefaultConfValuesGetResponse> map, String env, String inst) {
        Meta meta = new Meta();
        meta.setEnvironment(env);
        meta.getToolchain().put("name", inst);

        if (map != null) {
            map.forEach((k, v) -> {
                if (v == null) return;
                String inputValue = input.inputWithDefault(toTitle(k), v.defaultValue());
                meta.getToolchain().put(k, inputValue);
            });
        }

        return meta;
    }

    private String toTitle(String value) {
        return StringUtils.capitalize(value) + ": ";
    }

    private String toSectionTitle(String value) {
        return new AttributedString((value + " section").toUpperCase(), AttributedStyle.DEFAULT.bold().underline()).toAnsi() + ":";
    }

}