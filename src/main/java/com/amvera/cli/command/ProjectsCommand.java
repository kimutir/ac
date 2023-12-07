package com.amvera.cli.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ProjectsCommand {

    @ShellMethod(
            key = "projects",
            value = "Returns list of projects"
    )
    public String projects() {
        return null;
    }

}
