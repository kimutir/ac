package com.amvera.cli.command.env;

import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

import java.util.List;

@Command(group = "Environment variables commands")
public class EnvCommand {
    private final EnvironmentService envService;
    private final ProjectService projectService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public EnvCommand(EnvironmentService envService, ProjectService projectService, AmveraTable amveraTable, ShellHelper helper) {
        this.envService = envService;
        this.projectService = projectService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @Command(command = "env", description ="Environment variables for specified project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String environment(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project
    ) {
        ProjectResponse projectResponse = projectService.findBy(project);
        List<EnvDTO> envs = envService.getEnvironment(projectResponse);
        helper.println("ENVIRONMENTS");
        return envs.isEmpty() ? "< empty >" : amveraTable.environments(envs);
    }

}
