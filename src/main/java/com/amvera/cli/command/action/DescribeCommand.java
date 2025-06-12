package com.amvera.cli.command.action;

import com.amvera.cli.client.CnpgClient;
import com.amvera.cli.client.ProjectClient;
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

@Command(command = "describe", group = "Project commands")
public class DescribeCommand {

    private final ProjectService projectService;
    private final ShellHelper helper;
    private final EnvironmentService environmentService;
    private final DomainService domainService;
    private final CnpgClient cnpgClient;
    private final ProjectClient projectClient;

    public DescribeCommand(
            ProjectService projectService,
            ShellHelper helper,
            EnvironmentService environmentService,
            DomainService domainService,
            CnpgClient cnpgClient,
            ProjectClient projectClient
    ) {
        this.projectService = projectService;
        this.helper = helper;
        this.environmentService = environmentService;
        this.domainService = domainService;
        this.cnpgClient = cnpgClient;
        this.projectClient = projectClient;
    }

    @Command(command = "project", alias = "describe projects", description = "Get info of projects")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void project(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Project slug",
                    required = true
            ) String slug
    ) {
        ProjectResponse project = projectClient.get(slug);

        helper.printlnTitle("Project info");
        projectService.renderTable(project);

        helper.printlnTitle("Environment variables");
        environmentService.renderTable(project);

        helper.printlnTitle("Domains");
        domainService.renderTable(project);
    }

    @Command(command = "postgresql", alias = {"describe postgre", "describe psql", "describe cnpg"}, description = "Get info of cnpg clusters")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void postgresql(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Cnpg cluster slug",
                    required = true
            ) String slug
    ) {
        CnpgResponse cnpg = cnpgClient.getDetails(slug);

        helper.printlnTitle("Project info");
        projectService.renderCnpgTable(slug, cnpg);

        helper.printlnTitle("Domains");
        domainService.renderTable(slug, cnpg.username(), ServiceType.POSTGRESQL);
    }

    @Command(command = "preconfigured", alias = "describe preconf", description = "Get info of preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void preconfigured(
            @Option(
                    longNames = "slug",
                    shortNames = 's',
                    arity = CommandRegistration.OptionArity.EXACTLY_ONE,
                    description = "Preconfigured service slug",
                    required = true
            ) String slug
    ) {
        ProjectResponse project = projectClient.get(slug);

        helper.printlnTitle("Project info");
        projectService.renderPreconfiguredTable(project);

        helper.printlnTitle("Environment variables");
        environmentService.renderTable(project);

        helper.printlnTitle("Domains");
        domainService.renderTable(project);
    }

}
