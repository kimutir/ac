package com.amvera.cli.command.action;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.*;
import com.amvera.cli.utils.*;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

import java.util.List;

@Command(command = "get", group = "Project commands")
public class GetCommand {
    private final ProjectService projectService;
    private final CnpgService cnpgService;
    private final TariffService tariffService;
    private final EnvironmentService envService;
    private final DomainService domainService;
    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;

    public GetCommand(
            ProjectService projectService, CnpgService cnpgService,
            TariffService tariffService,
            EnvironmentService envService,
            DomainService domainService,
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector
    ) {
        this.projectService = projectService;
        this.cnpgService = cnpgService;
        this.tariffService = tariffService;
        this.envService = envService;
        this.domainService = domainService;
        this.table = table;
        this.helper = helper;
        this.selector = selector;
    }

    @Command(command = "", description = "Get list of all projects including postgres and configured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void get() {
        List<ProjectResponse> projectList = projectService.getProjectListRequest();

        if (projectList.isEmpty()) {
            helper.printWarning("No projects found. You can start with 'amvera create'");
        } else {
            helper.print(table.projects(projectList));
        }
    }

    @Command(command = "project", alias = "projects", description = "Get list of projects")
    @CommandAvailability(provider = "userLoggedOutProvider")
    // todo: org.springframework.core.convert.ConversionFailedException
    public void project() {
        List<ProjectResponse> projectList = projectService.getProjectListRequest()
                .stream().filter(p -> p.getServiceType().equals(ServiceType.PROJECT))
                .toList();

        if (projectList.isEmpty()) {
            helper.printWarning("No projects found. You can start with 'amvera create project'");
        } else {
            helper.print(table.projects(projectList));
        }
    }

    @Command(command = "psql", alias = {"get postgresql", "get postgre", "get cnpg"}, description = "Get list of postgresql (cnpg) clusters")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void postgresql() {
        List<ProjectResponse> cnpgList = projectService.getProjectListRequest()
                .stream()
                .filter(p -> p.getServiceType().equals(ServiceType.POSTGRESQL))
                .toList();

        if (cnpgList.isEmpty()) {
            helper.printWarning("No postgres clusters found. You can start with 'amvera create psql'");
        } else {
            helper.print(table.projects(cnpgList));
        }
    }

    @Command(command = "preconfigured", alias = {"get conf", "get preconf"}, description = "Get list of preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void preconfigured() {
        List<ProjectResponse> cnpgList = projectService.getProjectListRequest()
                .stream()
                .filter(p -> p.getServiceType().equals(ServiceType.PRECONFIGURED))
                .toList();

        if (cnpgList.isEmpty()) {
            helper.printWarning("No preconfigured projects found. You can start with 'amvera create conf'");
        } else {
            helper.print(table.projects(cnpgList));
        }
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
