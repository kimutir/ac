package com.example.springshell;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Command;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


//    @Bean
//    public CommandRegistration exit(CommandRegistration.BuilderSupplier builder) {
//        return builder.get()
//                .command("quit", "exit")
//                .withTarget()
//                .function(ctx -> {
////                    System.exit(0);
//                    return "THIS IS EXIT";
//                })
//                .and()
//                .interactionMode(InteractionMode.INTERACTIVE)
//                .build();
//    }
    @Bean
    public CommandRegistration test(CommandRegistration.BuilderSupplier builder) {
        return builder.get()
                .command("test")
                .withTarget()
                .function(ctx -> {
                    return "THIS IS TEST";
                })
                .and()
                .interactionMode(InteractionMode.INTERACTIVE)
                .build();
    }

//    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"}, interactionMode = InteractionMode.INTERACTIVE)
//    public void quit() {
//        throw new ExitRequest();
//    }

}
