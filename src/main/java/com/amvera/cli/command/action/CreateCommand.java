package com.amvera.cli.command.action;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.project.ProjectPostResponse;
import com.amvera.cli.dto.project.ProjectRequest;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.dto.project.cnpg.CnpgPostRequest;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.dto.project.config.*;
import com.amvera.cli.service.MarketplaceService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.Pair;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.input.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.CnpgTableModel;
import com.amvera.cli.utils.table.MarketplaceTableModel;
import com.amvera.cli.utils.table.ProjectTableModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Command(command = "create", alias = "create", group = "Create commands")
public class CreateCommand extends AbstractShellComponent {
    private final ProjectService projectService;
    private final MarketplaceService marketplaceService;
    private final ShellHelper helper;
    private final AmveraTable amveraTable;
    private final AmveraSelector selector;
    private final AmveraInput input;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    private static final String MARKETPLACE_VERSION = "1";

    public CreateCommand(
            ProjectService projectService,
            MarketplaceService marketplaceService,
            ShellHelper helper,
            AmveraTable amveraTable,
            AmveraSelector selector,
            AmveraInput input,
            Endpoints endpoints, AmveraHttpClient client
    ) {
        this.projectService = projectService;
        this.marketplaceService = marketplaceService;
        this.helper = helper;
        this.amveraTable = amveraTable;
        this.selector = selector;
        this.input = input;
        this.endpoints = endpoints;
        this.client = client;
    }

    @Command(command = "", description = "Add new project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void create() {
        int serviceTypeId = selector.selectServiceType();

        switch (ServiceType.valueOf(serviceTypeId)) {
            case PROJECT -> createProject();
            case PRECONFIGURED -> createPreconfigured();
            case POSTGRESQL -> createCnpg();
            case null -> {
                helper.printError("Invalid service type");
                System.exit(0);
            }
        }
    }

    @Command(command = "project", description = "Create new project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void project(
            @Option(longNames = "config", shortNames = 'c', description = "Add configuration amvera.yml") Boolean config
    ) throws JsonProcessingException {
        createProject();
    }

    @Command(command = "postgresql", alias = {"create psql", "create postgre"}, description = "Create postgres (cnpg) cluster")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void postgresql() {
        createCnpg();
    }

    @Command(command = "preconfigured", alias = "create preconf", description = "Create preconfigured service from marketplace")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void preconfigured() {
        createPreconfigured();
    }

    private void createPreconfigured() {
        Pair<String, Integer> serviceNameAndTariff = getServiceNameAndTariff();
        String serviceName = serviceNameAndTariff.first();
        int tariffId = serviceNameAndTariff.second();

        MarketplaceConfigResponse configTemplate = marketplaceService.getMarketplaceConfig();

        MarketplaceConfigPostRequest config = marketConfig(configTemplate, serviceName, tariffId);
        marketplaceService.saveMarketplaceConfig(config);

        ProjectResponse project = projectService.findBy(serviceName);
        helper.print(amveraTable.singleEntityTable(new MarketplaceTableModel(project, Tariff.value(tariffId))));

    }

    private void createCnpg() {
        Pair<String, Integer> serviceNameAndTariff = getServiceNameAndTariff();
        String serviceName = serviceNameAndTariff.first();
        int tariffId = serviceNameAndTariff.second();

        String database = input.notBlankOrNullInput("Enter database name: ");
        String username = input.notBlankOrNullInput("Enter database username: ");
        String password = input.notBlankOrNullInput("Enter database password: ");
        String superUserPassword = null;

        boolean superUserEnabled = selector.yesOrNoSingleSelector("Would you like to enable super user access?");

        if (superUserEnabled) {
            superUserPassword = input.notBlankOrNullInput("Enter super user password: ");
        }

        CnpgResponse cnpg = client.post(
                URI.create(endpoints.postgresql()),
                CnpgResponse.class,
                "Error during creation postgresql",
                new CnpgPostRequest(
                        serviceName,
                        tariffId,
                        null,
                        database,
                        username,
                        password,
                        1,
                        superUserEnabled,
                        superUserPassword
                )
        );

        helper.print(amveraTable.singleEntityTable(new CnpgTableModel(Objects.requireNonNull(cnpg), Tariff.value(tariffId))));
    }

    private void createProject() {
        Pair<String, Integer> serviceNameAndTariff = getServiceNameAndTariff();
        String serviceName = serviceNameAndTariff.first();
        int tariffId = serviceNameAndTariff.second();
        ProjectPostResponse project;

        boolean addConfig = selector.yesOrNoSingleSelector("Would you like to add configuration?");

        if (addConfig) {
            ConfigResponse configTemplate = client.get(
                    URI.create(endpoints.configurations()),
                    ConfigResponse.class,
                    "Error when getting config"
            );

            if (configTemplate != null) {
                AmveraConfiguration config = yamlConfig(configTemplate);

                project = client.post(
                        URI.create(endpoints.projects()),
                        ProjectPostResponse.class,
                        String.format("Error when creating %s", serviceName),
                        new ProjectRequest(serviceName, tariffId)
                );
                client.post(
                        UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/config")
                                .queryParam("slug", project.slug())
                                .build(project.slug()),
                        String.format("Error when saving %s configuration", project.slug()),
                        config
                );
                helper.print(amveraTable.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariffId))));
            } else {
                project = client.post(
                        URI.create(endpoints.projects()),
                        ProjectPostResponse.class,
                        String.format("Error when creating %s", serviceName),
                        new ProjectRequest(serviceName, tariffId)
                );
                helper.print(amveraTable.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariffId))));
                helper.printWarning("Project has been created. But you have to add configuration manually.");
            }

        } else {
            project = client.post(
                    URI.create(endpoints.projects()),
                    ProjectPostResponse.class,
                    String.format("Error when creating %s", serviceName),
                    new ProjectRequest(serviceName, tariffId)
            );
        }

        helper.print(amveraTable.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariffId))));
    }

    private AmveraConfiguration yamlConfig(ConfigResponse template) {
        AmveraConfiguration config = new AmveraConfiguration();

        List<SelectorItem<String>> environments = template.availableParameters()
                .keySet().stream().map(i -> SelectorItem.of(i, i)).toList();
        String selectedEnvironment = selector.singleSelector(environments, "Environment: ", true);

        List<SelectorItem<String>> instruments = template.availableParameters()
                .get(selectedEnvironment).keySet().stream().map(i -> SelectorItem.of(i, i)).toList();
        String selectedInstrument = selector.singleSelector(instruments, "Instrument: ", true);

        // meta section
        Map<String, DefaultConfValuesResponse> metaMap = template.availableParameters().get(selectedEnvironment).get(selectedInstrument).get("meta");
        configMetaSection(config, metaMap, selectedEnvironment, selectedInstrument);

        // build section
        Map<String, DefaultConfValuesResponse> buildMap = template.availableParameters().get(selectedEnvironment).get(selectedInstrument).get("build");
        configBuildSection(config, buildMap);

        // run section
        Map<String, DefaultConfValuesResponse> runMap = template.availableParameters().get(selectedEnvironment).get(selectedInstrument).get("run");
        configRunSection(config, runMap);

        return config;
    }

    private MarketplaceConfigPostRequest marketConfig(MarketplaceConfigResponse template, String serviceName, int tariffId) {
        MarketplaceConfigPostRequest config = new MarketplaceConfigPostRequest();

        config.setVersion(MARKETPLACE_VERSION);
        config.setName(serviceName);
        config.setTariffId(tariffId);

        List<SelectorItem<String>> serviceTypes = template.availableParameters()
                .keySet().stream().map(i -> SelectorItem.of(i, i)).toList();

        String selectedServiceType = selector.singleSelector(serviceTypes, "Service type: ", true);

        List<SelectorItem<String>> service = template.availableParameters().get(selectedServiceType)
                .keySet().stream().map(i -> SelectorItem.of(i, i)).toList();

        String selectedService = selector.singleSelector(service, "Service: ", true);

        Map<String, DefaultConfValuesResponse> metaSection = template.availableParameters()
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

        Map<String, DefaultConfValuesResponse> runSection = template.availableParameters()
                .get(selectedServiceType).get(selectedService).get(MARKETPLACE_VERSION).get("run");

        if (runSection != null) {
            helper.println(toSectionTitle("run"));
            Map<String, Object> runMap = new HashMap<>();
            String runArgs = input.inputWithDefault("Args: ", runSection.get("args").defaultValue());

            runMap.put("args", runArgs.isBlank() ? null : runArgs);
            config.setRun(runMap);
        } else {
            config.setRun(new HashMap<>());
        }

        Map<String, DefaultConfValuesResponse> envsSection = template.availableParameters()
                .get(selectedServiceType).get(selectedService).get(MARKETPLACE_VERSION).get("envvars");

        if (envsSection != null) {
            helper.println(toSectionTitle("envs"));
            List<EnvPostRequest> envs = new ArrayList<>();
            boolean isSecret;

            for (Map.Entry<String, DefaultConfValuesResponse> entry : envsSection.entrySet()) {
                String secretKey = entry.getKey();
                DefaultConfValuesResponse secretValue = entry.getValue();
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

    private void configRunSection(AmveraConfiguration config, Map<String, DefaultConfValuesResponse> map) {
        if (map == null) return;
        helper.println(toSectionTitle("run"));
        map.forEach((k, v) -> {
            if (v == null) return;
            String inputValue = input.inputWithDefault(toTitle(k), v.defaultValue());
            if (inputValue == null || inputValue.isBlank()) return;
            config.getRun().put(k, inputValue);
        });
    }

    private void configBuildSection(AmveraConfiguration config, Map<String, DefaultConfValuesResponse> map) {
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

    private void configMetaSection(AmveraConfiguration config, Map<String, DefaultConfValuesResponse> map, String env, String inst) {
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

        config.setMeta(meta);
    }

    private Pair<String, Integer> getServiceNameAndTariff() {
        String name = input.notBlankOrNullInput("Enter project name: ");
        int tariffId = selector.selectTariff();

        return new Pair<>(name, tariffId);
    }


    private String toTitle(String value) {
        return StringUtils.capitalize(value) + ": ";
    }

    private String toSectionTitle(String value) {
        return new AttributedString((value + " section").toUpperCase(), AttributedStyle.DEFAULT.bold().underline()).toAnsi() + ":";
    }
}