package com.example.springshell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.ShellApplicationRunner;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.SingleItemSelector.*;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@ShellComponent
public class HelloCommand extends AbstractShellComponent {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(key = "login", interactionMode = InteractionMode.ALL)
    public void login(
            @ShellOption(defaultValue = "") String email,
            @ShellOption(defaultValue = "") String password
    ) throws JsonProcessingException {
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("user-email")
                .name("Почта:")
                .defaultValue("")
                .and()
                .withStringInput("user-password")
                .name("Пароль:")
                .defaultValue("")
                .and()
                .build();

        ComponentContext<?> context = flow.run().getContext();
        String userEmail = context.get("user-email", String.class);
        String userPassword = context.get("user-password", String.class);

//        String userEmail = "kimutir@gmail.com";
//        String userPassword = "Ch3sh1r3";

        String url = "https://id.amvera.ru/auth/realms/amvera/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        LinkedMultiValueMap<String, String> properties = new LinkedMultiValueMap<>();

        properties.add("client_id", "amvera-web");
        properties.add("username", userEmail);
        properties.add("password", userPassword);
        properties.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(properties, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        String responseBody = response.getBody();

        System.out.println(responseBody);

//        TokenResponse tokenResponse = mapper.readValue(responseBody, TokenResponse.class);
//
//        System.out.println(tokenResponse.getAccess_token());
//        System.out.println(tokenResponse.getNot_before_policy());

        System.exit(0);
    }

    @ShellMethod(value = "Create new project", group = "Create", key = "create-project", interactionMode = InteractionMode.NONINTERACTIVE)
    public void create() {

        Map<String, String> environmentComponent = new HashMap<>();
        environmentComponent.put("Python", "python");
        environmentComponent.put("Java/Kotlin", "jvm");

        Map<String, String> builderPythonComponent = new HashMap<>();
        builderPythonComponent.put("PIP", "pip");

        Map<String, String> builderJVMComponent = new HashMap<>();
        builderJVMComponent.put("Maven", "maven");
        builderJVMComponent.put("Gradle", "gradle");

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("project-name")
                    .name("Название проекта:")
                    .defaultValue("project-name")
                    .and()
                .withSingleItemSelector("environment")
                    .name("Выберите окружение:")
                    .selectItems(environmentComponent)
                    .next(ctx -> ctx.getResultItem().get().getItem())
                    .and()
                .withSingleItemSelector("python")
                    .name("Выберите инструмент:")
                    .selectItems(builderPythonComponent)
                    .next(ctx -> null)
                    .and()
                .withSingleItemSelector("jvm")
                    .name("Выберите инструмент:")
                    .selectItems(builderJVMComponent)
                    .next(ctx -> null)
                    .and()
                .build();
        ComponentContext<?> context = flow.run().getContext();

        System.out.println(context.stream().toList());

        System.exit(0);
    }

    @ShellMethod(key = "flow showcase2", value = "Showcase with options", group = "Flow", interactionMode = InteractionMode.NONINTERACTIVE)
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




    @ShellMethod(key = "component string", value = "String input", group = "Components")
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

    @ShellMethod(key = "flow conditional", value = "Second component based on first", group = "Flow", interactionMode = InteractionMode.ALL)
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
