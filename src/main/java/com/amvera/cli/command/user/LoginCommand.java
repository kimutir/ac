package com.amvera.cli.command.user;

import com.amvera.cli.service.AuthService;
import com.amvera.cli.utils.AmveraInput;
import com.amvera.cli.utils.ShellHelper;
import org.jline.reader.MaskingCallback;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;

import java.io.IOException;

@ShellComponent
public class LoginCommand extends AbstractShellComponent {

    private final ComponentFlow.Builder componentFlowBuilder;
    private final AuthService authService;
    private final ShellHelper helper;
    private final AmveraInput input;


    public LoginCommand(
            ComponentFlow.Builder componentFlowBuilder,
            AuthService authService,
            ShellHelper helper,
            AmveraInput input) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.authService = authService;
        this.helper = helper;
        this.input = input;
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
    ) throws IOException {

//        helper.print("Название проекта: ");
//        String p = new AttributedString("Название проекта: ", AttributedStyle.DEFAULT.bold()).toAnsi();
//
//        String s = new LineReaderImpl(terminal).readLine(p, null, new TestMask(), null);
//        System.out.println("sout: " + s);

        String u = input.defaultInput("Username: ");
        String p = input.secretInput("Password: ");

        System.out.println(u + " " + p);

        user = "kimutir@gmail.com";
        password = "Ch3sh1r3";
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

    class TestMask implements MaskingCallback {

        @Override
        public String display(String line) {

            if (line == null || line.isBlank()) {
                return "<enter value>";
            }

            return line;
        }

        @Override
        public String history(String line) {
            return null;
        }
    }

}
