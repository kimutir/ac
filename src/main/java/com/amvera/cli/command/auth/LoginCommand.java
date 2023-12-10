package com.amvera.cli.command.auth;

import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.exception.CustomException;
import com.amvera.cli.service.AuthService;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.terminal.Terminal;
import org.springframework.http.*;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

@ShellComponent
//@RegisterReflectionForBinding
public class LoginCommand extends AbstractShellComponent {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final AuthService authService;
    private final Terminal terminal;

    public LoginCommand(RestTemplate restTemplate, ObjectMapper mapper, ComponentFlow.Builder componentFlowBuilder, AuthService authService, Terminal terminal) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.componentFlowBuilder = componentFlowBuilder;
        this.authService = authService;
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

//        user = "kimutir@gmail.com";
//        password = "Ch3sh1r3";

        try {
            if (user == null || user.isBlank()) {
                ComponentFlow emailFlow = componentFlowBuilder.clone().reset()
                        .withStringInput("user")
                        .name("Username/email:")
                        .defaultValue("")
                        .and().build();

                user = emailFlow.run().getContext().get("user");
            }

            if (password == null || password.isBlank()) {
                ComponentFlow passwordFlow = componentFlowBuilder.clone().reset()
                        .withStringInput("password")
                        .name("Password:")
                        .defaultValue("")
                        .and().build();

                password = passwordFlow.run().getContext().get("password");
            }
        } catch (Error e) {
            System.exit(0);
            throw new InterruptedIOException();
        }

        AuthResponse response = authService.login(user, password).block();
        if (response != null) {
            return response.getAccessToken();
        }

        return null;

    }


    @ShellMethod(key = "flow showcase2", value = "Showcase with options", interactionMode = InteractionMode.ALL)
    public String showcase2(
            @ShellOption(help = "Field1 value", defaultValue = ShellOption.NULL) String field1,
            @ShellOption(help = "Field2 value", defaultValue = ShellOption.NULL) String field2,
            @ShellOption(help = "Confirmation1 value", defaultValue = ShellOption.NULL) Boolean confirmation1,
            @ShellOption(help = "Path1 value", defaultValue = ShellOption.NULL) String path1,
            @ShellOption(help = "Single1 value", defaultValue = ShellOption.NULL) String single1,
            @ShellOption(help = "Multi1 value", defaultValue = ShellOption.NULL) List<String> multi1
    ) {
        Map<String, String> single1SelectItems = new HashMap<>();
        single1SelectItems.put("key1", "value1");
        single1SelectItems.put("key2", "value2");
        List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
                SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
        List<String> multi1ResultValues = multi1 != null ? multi1 : new ArrayList<>();
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("field1")
                .name("Field1")
                .defaultValue("defaultField1Value")
                .resultValue(field1)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .withStringInput("field2")
                .name("Field2")
                .resultValue(field2)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .withConfirmationInput("confirmation1")
                .name("Confirmation1")
                .resultValue(confirmation1)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .withPathInput("path1")
                .name("Path1")
                .resultValue(path1)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .withSingleItemSelector("single1")
                .name("Single1")
                .selectItems(single1SelectItems)
                .resultValue(single1)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .withMultiItemSelector("multi1")
                .name("Multi1")
                .selectItems(multi1SelectItems)
                .resultValues(multi1ResultValues)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        StringBuilder buf = new StringBuilder();
        result.getContext().stream().forEach(e -> {
            buf.append(e.getKey());
            buf.append(" = ");
            buf.append(e.getValue());
            buf.append("\n");
        });
        return buf.toString();
    }


    @ShellMethod(key = "component string", value = "String input")
    public String stringInput(boolean mask) {
        StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue");
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if (mask) {
            component.setMaskCharacter('*');
        }
        StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
        return "Got value " + context.getResultValue();
    }

    @ShellMethod(key = "flow conditional", value = "Second component based on first", interactionMode = InteractionMode.ALL)
    public void conditional() {
        Map<String, String> single1SelectItems = new HashMap<>();
        single1SelectItems.put("Field1", "field1");
        single1SelectItems.put("Field2", "field2");
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("single1")
                .name("Single1")
                .selectItems(single1SelectItems)
                .next(ctx -> ctx.getResultItem().get().getItem())
                .and()
                .withStringInput("field1")
                .name("Field1")
                .defaultValue("defaultField1Value")
                .next(ctx -> null)
                .and()
                .withStringInput("field2")
                .name("Field2")
                .defaultValue("defaultField2Value")
                .next(ctx -> null)
                .and()
                .build();
        flow.run();
    }


}
