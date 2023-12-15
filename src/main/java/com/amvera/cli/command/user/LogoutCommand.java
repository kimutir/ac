package com.amvera.cli.command.user;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class LogoutCommand {

    @ShellMethod(
            key = "logout",
            value = "Logout amvera cloud"
    )
    public String logout() {

        //todo: delete token and find the way to logout in keycloak
        return "Logged out successfully!";
    }

}
