package com.amvera.cli.command.info;

import com.amvera.cli.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.stereotype.Component;

@Component
@Command(group = "Built-In Commands")
public class WhoAmICommand {
    private final AuthService authService;

    @Autowired
    public WhoAmICommand(AuthService authService) {
        this.authService = authService;
    }

    @Command(command = "whoami", description = "Show current user information")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void who() {
        authService.info();
    }

}
