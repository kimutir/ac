package com.amvera.cli.command.user;

import com.amvera.cli.utils.TokenUtils;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command(group = "User commands")
public class LogoutCommand {
    @Command(command = "logout", description = "Logout amvera cloud")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String logout() {
        return TokenUtils.deleteToken();
    }
}
