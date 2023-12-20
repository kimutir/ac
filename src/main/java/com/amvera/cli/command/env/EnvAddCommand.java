package com.amvera.cli.command.env;

import com.amvera.cli.dto.project.*;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;

import java.util.ArrayList;
import java.util.List;

@Command(group = "Environment variables commands")
public class EnvAddCommand {
    private final ProjectService projectService;
    private final EnvironmentService envService;
    private final ShellHelper helper;
    private final AmveraTable table;
    private final ComponentFlow.Builder componentFlowBuilder;

    public EnvAddCommand(
            ProjectService projectService,
            EnvironmentService envService,
            ShellHelper helper,
            AmveraTable table,
            ComponentFlow.Builder componentFlowBuilder) {
        this.projectService = projectService;
        this.envService = envService;
        this.helper = helper;
        this.table = table;
        this.componentFlowBuilder = componentFlowBuilder;
    }

    @Command(command = "env-add", description = "Add or update environment variables")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void change(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        ProjectGetResponse p = projectService.findBy(project);
        String slug = p.getSlug();
        helper.println("ENVIRONMENTS");
        List<EnvDTO> envs = envService.getEnvironmentBySlug(slug);
        helper.println(envs.isEmpty() ? "< empty >" : table.environments(envs));

        boolean more = true;

        List<EnvPostRequest> toPost = new ArrayList<>();
        List<EnvPutRequest> toUpdate = new ArrayList<>();

        while (more) {
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
                    .withConfirmationInput("more")
                    .defaultValue(false)
                    .name("One more?")
                    .and()
                    .build()
                    .run().getContext();

            Boolean secret = context.get("secret");
            String name = context.get("name");
            String value = context.get("value");

            boolean post = true;

            for (EnvDTO e : envs) {
                if (e.name().equalsIgnoreCase(name)) {
                    if (e.isSecret().equals(secret)) {
                        toUpdate.add(new EnvPutRequest(e.id(), name, value, secret));
                    }
                    post = false;
                    break;
                }
            }

            if (name == null || name.isBlank() || value == null || value.isBlank()) {
                post = false;
            }

            if (post) {
                toPost.add(new EnvPostRequest(name, value, secret));
            }

            more = context.get("more");
        }

        toPost.forEach(e -> envService.addEnvironment(e, slug));
        toUpdate.forEach(e -> envService.updateEnvironment(e, slug));

        helper.println("NEW ENVIRONMENTS");
        envs = envService.getEnvironmentBySlug(slug);
        helper.println(envs.isEmpty() ? "< empty >" : table.environments(envs));
    }

}
