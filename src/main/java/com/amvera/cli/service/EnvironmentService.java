package com.amvera.cli.service;

import com.amvera.cli.client.EnvClient;
import com.amvera.cli.client.ProjectClient;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.EnvSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentService {
    private final ShellHelper helper;
    private final AmveraTable table;
    private final AmveraSelector selector;
    private final ProjectService projectService;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final EnvClient envClient;
    private final ProjectClient projectClient;

    public EnvironmentService(
            ShellHelper helper,
            AmveraTable table,
            AmveraSelector selector,
            ProjectService projectService,
            ComponentFlow.Builder componentFlowBuilder,
            EnvClient envClient,
            ProjectClient projectClient
    ) {
        this.helper = helper;
        this.table = table;
        this.selector = selector;
        this.projectService = projectService;
        this.componentFlowBuilder = componentFlowBuilder;
        this.envClient = envClient;
        this.projectClient = projectClient;
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

        envClient.create(new EnvPostRequest(name, value, secret), project.getSlug());
        helper.printInfo("Environment has been created. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void delete(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);

        EnvResponse env = select(project.getSlug());

        envClient.delete(env.id(), project.getSlug());
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

        envClient.update(new EnvPutRequest(env.id(), name, value, env.isSecret()), project.getSlug());
        helper.printInfo("Environment has been updated. Do not forget to restart project to apply it.");

        renderTable(project);
    }

    public void renderTable(String slug) {
        ProjectResponse project;

        if (slug == null) {
            project = projectService.select();
        } else {
            project = projectClient.get(slug);
        }

        renderTable(project);
    }

    public void renderTable(ProjectResponse project) {
        List<EnvResponse> envList = envClient.get(project.getSlug());

        if (envList.isEmpty()) {
            helper.printWarning("No environment found. You can add environment by 'amvera create env'");
        } else {
            helper.print(table.environments(envList));
        }
    }

    public EnvResponse select(String slug) {
        List<SelectorItem<EnvSelectItem>> projectList = envClient.get(slug)
                .stream()
                .map(EnvResponse::toSelectorItem).toList();

        return selector.singleSelector(projectList, "Select environment: ", true).getEnv();
    }

}
