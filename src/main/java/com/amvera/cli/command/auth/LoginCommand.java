package com.amvera.cli.command.auth;

import com.amvera.cli.service.AuthService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import org.springframework.cglib.core.Local;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

@ShellComponent
public class LoginCommand extends AbstractShellComponent {

    private final ComponentFlow.Builder componentFlowBuilder;
    private final AuthService authService;
    private final ShellHelper helper;
    private final Terminal terminal;

    public LoginCommand(ComponentFlow.Builder componentFlowBuilder, AuthService authService, ShellHelper helper, Terminal terminal) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.authService = authService;
        this.helper = helper;
        this.terminal = terminal;
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

        String value = System.console().readLine("Enter value for --интер: " );
        System.out.println(value);
        String ru = System.console().readLine("Enter value for --интер: ");
        System.out.println("sout" + ru);
        helper.print("terminal" + ru);

        StringInput stringInput = new StringInput(terminal, "input", "дефолт");
        stringInput.setResourceLoader(getResourceLoader());
        stringInput.addPreRunHandler(ctx -> {
            String result = ctx.getResultValue();
            System.out.println(result);
        });
        stringInput.setTemplateExecutor(getTemplateExecutor());
        stringInput.run(StringInput.StringInputContext.empty());
//        user = "kimutir@gmail.com";
//        password = "Ch3sh1r3";
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

        Charset encoding = getTerminal().encoding();
        System.out.println(encoding.toString());


        authService.login(user, password);

        return "Authorized successfully!";
    }

}
