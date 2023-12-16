package com.amvera.cli.command.user;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command
public class LogoutCommand {
    @Command(command = "logout", description = "Logout amvera cloud")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String logout() {

        //todo: delete token and find the way to logout in keycloak
        return "Logged out successfully!";
    }

}
