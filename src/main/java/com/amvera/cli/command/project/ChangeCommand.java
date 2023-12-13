package com.amvera.cli.command.project;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ChangeCommand {

    @ShellMethod(
            key = "project change"
    )
    public void change(
            @ShellOption(value = {"-p", "--project"}) String project
    ) {
        System.out.println("project option: " + project);
        System.out.println("project change command");
    }
}
