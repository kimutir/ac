package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.EnvSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class EnvironmentService {
    private final ShellHelper helper;
    private final AmveraTable table;
    private final AmveraSelector selector;
    private final ProjectService projectService;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    public EnvironmentService(
            ShellHelper helper,
            AmveraTable table,
            AmveraSelector selector,
            ProjectService projectService,
            ComponentFlow.Builder componentFlowBuilder,
            Endpoints endpoints, AmveraHttpClient client
    ) {
        this.helper = helper;
        this.table = table;
        this.selector = selector;
        this.projectService = projectService;
        this.componentFlowBuilder = componentFlowBuilder;
        this.endpoints = endpoints;
        this.client = client;
    }

    public void create(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug, p -> !p.getServiceType().equals(ServiceType.POSTGRESQL));

        ComponentContext<?> context = componentFlowBuilder.clone().reset()
                .withConfirmationInput("secret")
                .defaultValue(false)
                .name("Is secret?")
                .and()
                .withStringInput("name")
                .name("Name:")
                .and()
                .withStringInput("value")
                .name("Value:")
                .and()
                .build()
                .run().getContext();

        Boolean secret = context.get("secret");
        String name = context.get("name");
        String value = context.get("value");

        if (name == null || name.isBlank() || value == null || value.isBlank()) {
            // todo throw exception
        }

        client.post(
                UriComponentsBuilder.fromUriString(endpoints.env() + "/{slug}").build(project.getSlug()),
                EnvResponse.class,
                new EnvPostRequest(name, value, secret),
                String.format("Error when adding env to %s", project.getSlug())
        );

        helper.printInfo("Environment has been created. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void delete(String slug, Long id) {

    }

    public void delete(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);

        EnvResponse env = select(project.getSlug());

        client.delete(
                UriComponentsBuilder.fromUriString(endpoints.env() + "/{slug}/{id}").build(project.getSlug(), env.id()),
                "Error when deleting env"
        );

        helper.printInfo("Environment has been deleted.");

        renderTable(project);
    }

    public void update(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);

        EnvResponse env = select(project.getSlug());

        ComponentContext<?> context = componentFlowBuilder.clone().reset()
                .withStringInput("name")
                .name("Name:")
                .and()
                .withStringInput("value")
                .name("Value:")
                .and()
                .build()
                .run().getContext();

        String name = context.get("name");
        String value = context.get("value");

        if (name == null || name.isBlank() || value == null || value.isBlank()) {
            // todo throw exception
        }


        client.put(
                UriComponentsBuilder.fromUriString(endpoints.env() + "/{slug}/{id}").build(project.getSlug(), env.id()),
                new EnvPutRequest(env.id(), name, value, env.isSecret()),
                String.format("Error when updating %s env", env.name())
        );

        helper.printInfo("Environment has been updated. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void renderTable(String slug) {
        ProjectResponse project;

        if (slug == null) {
            project = projectService.select(p -> !p.getServiceType().equals(ServiceType.POSTGRESQL));
        } else {
            project = client.get(
                    UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(slug),
                    ProjectResponse.class,
                    String.format("Could not find %s", slug)
            );
        }

        renderTable(project);
    }

    public void renderTable(ProjectResponse project) {
        List<EnvResponse> envList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.env() + "/{slug}").build(project.getSlug()),
                new ParameterizedTypeReference<>() {
                },
                "Error when getting env list"
        );

        if (envList.isEmpty()) {
            helper.printWarning("No environment found. You can add environment by 'amvera env add'");
        } else {
            helper.print(table.environments(envList));
        }
    }

    public EnvResponse select(String slug) {
        List<SelectorItem<EnvSelectItem>> envList = client.get(
                        UriComponentsBuilder.fromUriString(endpoints.env() + "/{slug}").build(slug),
                        new ParameterizedTypeReference<List<EnvResponse>>() {
                        },
                        "Error when getting env list"
                )
                .stream()
                .map(EnvResponse::toSelectorItem).toList();

        if (envList.isEmpty())
            throw new EmptyValueException("No environments found. You can add environment by 'amvera env add'");

        return selector.singleSelector(envList, "Select environment: ", true).getEnv();
    }

}
