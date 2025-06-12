package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.utils.*;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.EnvSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentService {
    private final AmveraHttpClient client;
    private final ShellHelper helper;
    private final AmveraTable table;
    private final AmveraSelector selector;
    private final ProjectService projectService;
    private final ComponentFlow.Builder componentFlowBuilder;

    public EnvironmentService(
            AmveraHttpClient client,
            ShellHelper helper,
            AmveraTable table, AmveraSelector selector, ProjectService projectService, ComponentFlow.Builder componentFlowBuilder
    ) {
        this.client = client;
        this.helper = helper;
        this.table = table;
        this.selector = selector;
        this.projectService = projectService;
        this.componentFlowBuilder = componentFlowBuilder;
    }

    public void create(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);

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

        createRequest(new EnvPostRequest(name, value, secret), project.getSlug());
        helper.printInfo("Environment has been created. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void delete(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);

        EnvResponse env = select(project.getSlug());

        deleteRequest(env.id(), project.getSlug());
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

        updateRequest(new EnvPutRequest(env.id(), name, value, env.isSecret()), project.getSlug());
        helper.printInfo("Environment has been updated. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void renderTable(String slug) {
        ProjectResponse project;

        if (slug == null) {
            project = projectService.select();
        } else {
            project = projectService.findBySlug(slug);
        }

        renderTable(project);
    }

    public void renderTable(ProjectResponse project) {
        List<EnvResponse> envList = getRequest(project.getSlug());

        if (envList.isEmpty()) {
            helper.printWarning("No environment found. You can add environment by 'amvera create env'");
        } else {
            helper.print(table.environments(envList));
        }
    }

    public ResponseEntity<EnvResponse> createRequest(EnvPostRequest req, String slug) {
        ResponseEntity<EnvResponse> response = client.environment()
                .post().uri("/{slug}", slug)
                .body(req)
                .retrieve()
                .toEntity(EnvResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to add environment variables.");
        }

        return response;
    }

    public void updateRequest(EnvPutRequest env, String slug) {
        ResponseEntity<EnvResponse> response = client.environment()
                .put().uri("/{slug}/{id}", slug, env.id())
                .body(env)
                .retrieve()
                .toEntity(EnvResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to update environment variables.");
        }
    }

    public void deleteRequest(Long id, String slug) {
        ResponseEntity<String> response = client.environment()
                .delete().uri("/{slug}/{id}", slug, id)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to delete environment variables.");
        }
    }

    public List<EnvResponse> getRequest(String slug) {
        ResponseEntity<List<EnvResponse>> envResponse = client.environment()
                .get().uri("/{slug}", slug)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });


        if (envResponse.getStatusCode().isError()) {
            // todo: throw exception
        }

        return envResponse.getBody();
    }

    public EnvResponse select(String slug) {
        List<SelectorItem<EnvSelectItem>> projectList = getRequest(slug)
                .stream()
                .map(EnvResponse::toSelectorItem).toList();

        return selector.singleSelector(projectList, "Select environment: ", true).getEnv();
    }

}
