package com.amvera.cli.command.action;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.service.DomainService;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.web.util.UriComponentsBuilder;

@Command(command = "describe", alias = "describe", group = "Project commands")
public class DescribeCommand {

    private final ProjectService projectService;
    private final ShellHelper helper;
    private final EnvironmentService environmentService;
    private final DomainService domainService;
    private final AmveraHttpClient client;
    private final Endpoints endpoints;

    public DescribeCommand(
            ProjectService projectService,
            ShellHelper helper,
            EnvironmentService environmentService,
            DomainService domainService,
            AmveraHttpClient client,
            Endpoints endpoints
    ) {
        this.projectService = projectService;
        this.helper = helper;
        this.environmentService = environmentService;
        this.domainService = domainService;
        this.client = client;
        this.endpoints = endpoints;
    }

    @Command(command = "project", description = "Returns project info")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void project(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Project slug",
                    required = true
            ) String slug
    ) {
        ProjectResponse project = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(slug),
                ProjectResponse.class,
                String.format("Could not find %s", slug)
        );

        helper.printlnTitle("Project info");
        projectService.renderTable(project);

        helper.printlnTitle("Environment variables");
        environmentService.renderTable(project);

        helper.printlnTitle("Domains");
        domainService.renderTable(project);
    }

    @Command(command = "postgresql", alias = {"postgre", "psql"}, description = "Returns postgres info")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void postgresql(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Cnpg cluster slug",
                    required = true
            ) String slug
    ) {
        CnpgResponse cnpg =client.get(
                UriComponentsBuilder
                        .fromUriString(endpoints.postgresql() + "/{slug}/details")
                        .build(slug),
                CnpgResponse.class,
                String.format("Unable to find '%s' postgres detailed info", slug)
        );

        helper.printlnTitle("Project info");
        projectService.renderCnpgTable(slug, cnpg);

        helper.printlnTitle("Domains");
        domainService.renderTable(slug, cnpg.username(), ServiceType.POSTGRESQL);
    }

    @Command(command = "preconfigured", alias = "preconf", description = "Returns preconfigured service info")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void preconfigured(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Preconfigured service slug",
                    required = true
            ) String slug
    ) {
        ProjectResponse project = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(slug),
                ProjectResponse.class,
                String.format("Could not find %s", slug)
        );

        helper.printlnTitle("Project info");
        projectService.renderPreconfiguredTable(project);

        helper.printlnTitle("Environment variables");
        environmentService.renderTable(project);

        helper.printlnTitle("Domains");
        domainService.renderTable(project);
    }

}
