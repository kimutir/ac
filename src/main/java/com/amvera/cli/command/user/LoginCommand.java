package com.amvera.cli.command.user;

import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.service.UserService;
import com.amvera.cli.utils.AmveraInput;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.AbstractShellComponent;

@Command(group = "User commands")
public class LoginCommand extends AbstractShellComponent {
    private final UserService authService;
    private final AmveraInput input;


    public LoginCommand(
            UserService authService,
            AmveraInput input) {
        this.authService = authService;
        this.input = input;
    }

    @Command(command = "login", description = "Login amvera cloud")
    @CommandAvailability(provider = "userLoggedProvider")
    public String login(
            @Option(longNames = "user", shortNames = 'u', arity = OptionArity.EXACTLY_ONE, description = "Username/email for authorization") String user,
            @Option(longNames = "password", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "User password for authorization") String password
    ) {
//        user = "kimutir@gmail.com";
//        password = "Ch3sh1r3";

        if (user == null || user.isBlank()) {
            user = input.defaultInput("Username/email: ");
        }

        if (user == null || user.isBlank()) {
            throw new EmptyValueException("Username can not be empty.");
        }

        if (password == null || password.isBlank()) {
            password = input.secretInput("Password: ");
        }

        if (password == null || password.isBlank()) {
            throw new EmptyValueException("Password can not be empty.");
        }


        // todo: WebFlux doesn't work on Windows after compilation to binary (MacOS - ok)
//        AuthResponse response = authService.login(user, password).block();
//        if (response != null) {
//            return response.getAccessToken();
//        }

        return authService.login(user, password);
    }

}
