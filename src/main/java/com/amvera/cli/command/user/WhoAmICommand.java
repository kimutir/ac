package com.amvera.cli.command.user;

import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.service.UserService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command(group = "User commands")
public class WhoAmICommand {
    private final UserService authService;
    private final ShellHelper helper;

    public WhoAmICommand(UserService authService, ShellHelper helper) {
        this.authService = authService;
        this.helper = helper;
    }

    @Command(command = "whoami", description = "Show current user information")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void who() {
        InfoResponse info = authService.info();

        String username = new AttributedString("Username: ", AttributedStyle.DEFAULT.bold()).toAnsi();
        String name = new AttributedString("First Name: ", AttributedStyle.DEFAULT.bold()).toAnsi();
        String lastName = new AttributedString("Last Name: ", AttributedStyle.DEFAULT.bold()).toAnsi();
        String email = new AttributedString("Email: ", AttributedStyle.DEFAULT.bold()).toAnsi();

        helper.println(username + info.username());
        helper.println(name + info.firstName());
        helper.println(lastName + info.lastName());
        helper.println(email + info.email());
    }

}
