package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.service.DomainService;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "get", alias = "get", group = "Project commands")
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

    @Command(command = "", description = "Returns list of all projects including postgres and configured services")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void get() {
        projectService.renderTable();
    }

    @Command(command = "project", alias = "projects", description = "Returns list of projects")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void project() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.PROJECT));
    }

    @Command(command = "psql", alias = {"postgresql", "postgre"}, description = "Returns list of postgresql (cnpg) clusters")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void postgresql() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.POSTGRESQL));
    }

    @Command(command = "preconfigured", alias = {"conf", "preconf"}, description = "Returns list of preconfigured services")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void preconfigured() {
        projectService.renderTable(p -> p.getServiceType().equals(ServiceType.PRECONFIGURED));
    }

    @Command(command = "domain", alias = "domains", description = "Returns list of domains")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void domain(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        domainService.renderTable(slug);
    }

    @Command(command = "env", alias = {"environment", "envs"}, description = "Returns list of environment")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void env(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        envService.renderTable(slug);
    }

}
