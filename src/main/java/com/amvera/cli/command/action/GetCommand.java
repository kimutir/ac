package com.amvera.cli.command.action;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.service.DomainService;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.service.TariffService;
import com.amvera.cli.utils.*;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;

@Command(command = "get", alias = "get", group = "Project commands")
public class GetCommand {
    private final ProjectService projectService;
    private final TariffService tariffService;
    private final EnvironmentService envService;
    private final DomainService domainService;
    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;

    public GetCommand(
            ProjectService projectService,
            TariffService tariffService,
            EnvironmentService envService,
            DomainService domainService,
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector
    ) {
        this.projectService = projectService;
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
        List<ProjectGetResponse> projectList = projectService.getProjects();

        if (projectList.isEmpty()) {
            helper.printWarning("No projects found. You can start with 'amvera create'");
        } else {
            helper.print(table.projects(projectList));
        }
    }

    @Command(command = "project", alias = "projects", description = "Get list of projects")
    @CommandAvailability(provider = "userLoggedOutProvider")
    // todo: org.springframework.core.convert.ConversionFailedException
    public void project(
            @Option(longNames = "id", shortNames = 'i', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String id,
            @Option(longNames = "env", shortNames = 'e', description = "Returns project environments") Boolean env
    ) {

        if (id != null) {
            ProjectGetResponse project = projectService.findBy(id);

            projectService.renderTable(project);

            envService.renderTable(project.getSlug());

            domainService.renderTable(project);
        } else {
            List<ProjectGetResponse> projects = projectService.getProjects()
                    .stream().filter(p -> p.getServiceType().equals(ServiceType.PROJECT))
                    .toList();

            helper.print(table.projects(projects));
        }

    }

    @Command(command = "postgresql", alias = {"psql", "postgre"}, description = "Get list of postgresql (cnpg) clusters")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void postgresql() {
        List<ProjectGetResponse> cnpgList = projectService.getProjects()
                .stream()
                .filter(p -> p.getServiceType().equals(ServiceType.POSTGRESQL))
                .toList();

        if (cnpgList.isEmpty()) {
            helper.printWarning("No postgres clusters found. You can start with 'amvera create psql'");
        } else {
            helper.print(table.projects(cnpgList));
        }
    }

    @Command(command = "preconfigured", alias = {"conf", "preconf"}, description = "Get list of preconfigured services")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void preconfigured() {
        List<ProjectGetResponse> cnpgList = projectService.getProjects()
                .stream()
                .filter(p -> p.getServiceType().equals(ServiceType.PRECONFIGURED))
                .toList();

        if (cnpgList.isEmpty()) {
            helper.printWarning("No preconfigured projects found. You can start with 'amvera create conf'");
        } else {
            helper.print(table.projects(cnpgList));
        }
    }

    @Command(command = "domain", alias = "domains", description = "Get list of domains")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void domain(
            @Option(longNames = "id", shortNames = 'i', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug") String id
    ) {
        ProjectGetResponse project;

        if (id != null) {
            project = projectService.findBy(id);
        } else {
            List<SelectorItem<ProjectSelectItem>> projectList = projectService.getProjects().stream().map(ProjectGetResponse::toSelectorItem).toList();
            ProjectSelectItem selected = selector.singleSelector(projectList, "Select project: ", true);
            project = selected.getProject();
        }

        domainService.renderTable(project);
    }

    @Command(command = "environment", alias = {"env", "envs"}, description = "Get list of environment")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void env() {
        List<SelectorItem<ProjectSelectItem>> projectList = projectService.getProjects().stream().map(ProjectGetResponse::toSelectorItem).toList();

        ProjectSelectItem selected = selector.singleSelector(projectList, "Select project: ", true);

        List<EnvDTO> envList = envService.getEnvironmentBySlug(selected.getProject().getSlug());

        if (envList.isEmpty()) {
            helper.printWarning("No environment found. You can add environment by 'amvera create env'");
        } else {
            helper.print(table.environments(envList));
        }
    }

}
