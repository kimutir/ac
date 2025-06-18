package com.amvera.cli.command.user;

import com.amvera.cli.service.UserService;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command(group = "User commands")
public class LogoutCommand {

    private final UserService userService;

    public LogoutCommand(UserService userService) {
        this.userService = userService;
    }

    @Command(command = "logout", description = "Logout amvera cloud")
    @CommandAvailability(provider = "userLoggedInProvider")
    public String logout() {
        return userService.logout();
    }
}
