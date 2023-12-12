package com.amvera.cli.command.auth;

import com.amvera.cli.service.AuthService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

@ShellComponent
public class LoginCommand extends AbstractShellComponent {

    private final ComponentFlow.Builder componentFlowBuilder;
    private final AuthService authService;
    private final ShellHelper helper;

    public LoginCommand(ComponentFlow.Builder componentFlowBuilder, AuthService authService, ShellHelper helper) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.authService = authService;
        this.helper = helper;
    }

    @ShellMethod(
            key = "login",
            interactionMode = InteractionMode.ALL,
            value = "Login amvera cloud"
    )
    public String login(
            @ShellOption(
                    defaultValue = ShellOption.NULL,
                    help = "Username/email for authorization",
                    value = {"-u", "--user"}
            ) String user,
            @ShellOption(
                    defaultValue = ShellOption.NULL,
                    help = "User password for authorization",
                    value = {"-p", "--password"}
            ) String password
    ) {
        user = "kimutir@gmail.com";
        password = "Ch3sh1r3";
        try {
            if (user == null || user.isBlank()) {
                ComponentFlow emailFlow = componentFlowBuilder.clone().reset()
                        .withStringInput("user")
                        .name("Username/email:")
                        .and().build();

                user = emailFlow.run().getContext().get("user");
            }

            if (password == null || password.isBlank()) {
                ComponentFlow passwordFlow = componentFlowBuilder.clone().reset()
                        .withStringInput("password")
                        .maskCharacter('*')
                        .name("Password:")
                        .and().build();

                password = passwordFlow.run().getContext().get("password");
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
