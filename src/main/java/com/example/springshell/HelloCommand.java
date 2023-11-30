package com.example.springshell;

import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.ShellApplicationRunner;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.AbstractShellComponent;

import java.io.IOException;
import java.util.*;

@ShellComponent
public class HelloCommand extends AbstractShellComponent {

    @Autowired
    private Terminal terminal;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(key = "hello"
            , interactionMode = InteractionMode.NONINTERACTIVE
    )
    public void greeting(@ShellOption(defaultValue = "stranger") String name) {
//        System.out.println("This is noninteractive mode.");
        System.out.println("Hello " + name);
        System.exit(0);
//        return "Hello " + name;
    }

    @ShellMethod(key = "create-project", interactionMode = InteractionMode.NONINTERACTIVE)
    public String create() throws IOException {
        return "This is all modes.";
    }

    @ShellMethod(key = "flow showcase2", value = "Showcase with options", group = "Flow")
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



    @ShellMethod(key = "login", interactionMode = InteractionMode.ALL)
    public void login(
            @ShellOption(defaultValue = "") String email,
            @ShellOption(defaultValue = "") String password
    ) {
        while (email == null || email.isBlank()) {
            email = System.console().readLine("Enter email: ").trim();
        }
        while (password == null || password.isBlank()) {
            password = System.console().readLine("Введите почту: ").trim();
        }

        System.out.println("cred: " + email + " " + password);
        System.exit(0);
    }

    @ShellMethod("Add array numbers.")
    public double addDoubles(@ShellOption(arity = 3) double[] numbers) {
        return Arrays.stream(numbers).sum();
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

    @ShellMethod(key = "flow conditional", value = "Second component based on first", group = "Flow", interactionMode = InteractionMode.NONINTERACTIVE)
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
