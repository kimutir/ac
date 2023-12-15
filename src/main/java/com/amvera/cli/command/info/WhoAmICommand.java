package com.amvera.cli.command.info;

import com.amvera.cli.service.AuthService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class WhoAmICommand {
    private final AuthService authService;

    public WhoAmICommand(AuthService authService) {
        this.authService = authService;
    }

    @ShellMethod(
            key = "whoami",
            value = "Shows user current user info"
    )
    public void who() {
        authService.info();
    }

}
