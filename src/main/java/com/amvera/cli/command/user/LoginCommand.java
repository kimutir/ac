package com.amvera.cli.command.user;

import com.amvera.cli.service.AuthService;
import com.amvera.cli.utils.AmveraInput;
import org.springframework.shell.command.CommandRegistration.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;

@Command
public class LoginCommand extends AbstractShellComponent {
    private final AuthService authService;
    private final AmveraInput input;


    public LoginCommand(
            AuthService authService,
            AmveraInput input) {
        this.authService = authService;
        this.input = input;
    }

    @Command(command = "login", description = "Login amvera cloud")
    @CommandAvailability(provider = "userLoggedProvider")
    public String login(
            @Option(longNames = "user", shortNames = 'u', defaultValue = ShellOption.NULL, arity = OptionArity.EXACTLY_ONE, description = "Username/email for authorization") String user,
            @Option(longNames = "password", shortNames = 'p', defaultValue = ShellOption.NULL, arity = OptionArity.EXACTLY_ONE, description = "User password for authorization") String password
    ) {

        user = "kimutir@gmail.com";
        password = "Ch3sh1r33";
        try {
            if (user == null || user.isBlank()) {
                user = input.defaultInput("Username/email: ");
            }
            if (password == null || password.isBlank()) {
                password = input.secretInput("Password: ");
            }
        } catch (Error e) {
            System.exit(0);
        }

        // todo: WebFlux doesn't work on Windows after compilation to binary (MacOS - ok)
//        AuthResponse response = authService.login(user, password).block();
//        if (response != null) {
//            return response.getAccessToken();
//        }

        authService.login(user, password);

        return "Authorized successfully!";
    }

}
