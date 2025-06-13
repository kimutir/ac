package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.service.DomainService;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "get", group = "Project commands")
public class GetCommand {
    private final ProjectService projectService;
    private final EnvironmentService envService;
    private final DomainService domainService;

    public GetCommand(
            ProjectService projectService,
            EnvironmentService envService,
            DomainService domainService
    ) {
        this.projectService = projectService;
        this.envService = envService;
        this.domainService = domainService;
    }

    @Command(command = "", description = "Get list of all projects including postgres and configured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void get() {
        projectService.renderTable();
    }

    @Command(command = "project", alias = "projects", description = "Get list of projects")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void project() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.PROJECT));
    }

    @Command(command = "psql", alias = {"get postgresql", "get postgre", "get cnpg"}, description = "Get list of postgresql (cnpg) clusters")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void postgresql() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.POSTGRESQL));
    }

    @Command(command = "preconfigured", alias = {"get conf", "get preconf"}, description = "Get list of preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void preconfigured() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.PRECONFIGURED));
    }

    @Command(command = "domain", alias = "get domains", description = "Get list of domains")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void domain(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        domainService.renderTable(slug);
    }

    @Command(command = "env", alias = {"get environment", "get envs"}, description = "Get list of environment")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void env(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.renderTable(slug);
    }

}
