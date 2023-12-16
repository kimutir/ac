package com.amvera.cli.command.user;

import org.springframework.shell.command.annotation.Command;

@Command
public class LogoutCommand {
    @Command(command = "logout", description = "Logout amvera cloud")
    public String logout() {

        //todo: delete token and find the way to logout in keycloak
        return "Logged out successfully!";
    }

}
