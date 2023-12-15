package com.amvera.cli.command.env;

import com.amvera.cli.dto.project.EnvDTO;
import com.amvera.cli.service.EnvironmentService;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class EnvCommand {

    private final EnvironmentService envService;
    private final AmveraTable amveraTable;
    private final ShellHelper helper;

    public EnvCommand(EnvironmentService envService, AmveraTable amveraTable, ShellHelper helper) {
        this.envService = envService;
        this.amveraTable = amveraTable;
        this.helper = helper;
    }

    @ShellMethod(key = "env", value = "Environment variables for srecified project")
    public String environment(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project
    ) {
        List<EnvDTO> envs = envService.getEnvironment(project);
        helper.println("ENVIRONMENTS");
        return amveraTable.environments(envs);
    }

}
