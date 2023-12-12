package com.amvera.cli.exception;

import com.amvera.cli.utils.PromptColor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

/**
 * todo: how to turn off logs for native-image? *doesn't catch some exceptions
 * Exception re
 */
public class CustomExceptionResolver implements CommandExceptionResolver {

//    private final ConfigurableApplicationContext application;
//
//    public CustomExceptionResolver(ConfigurableApplicationContext application) {
//        super();
//        this.application = application;
//    }

    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof CustomException) {
            return CommandHandlingResult.of("Hi, handled exception\n", 42);
        }
        if (e instanceof IOException) {
            return CommandHandlingResult.of(" io exception", 0);
        }
        if (e instanceof IllegalStateException) {
            return CommandHandlingResult.of(" state exception", 0);
        }
        if (e instanceof NumberFormatException) {
            return CommandHandlingResult.of("Format exception", 1);
        }
        //todo: add descriptions
//        if (e instanceof HttpClientErrorException) {
//            if (((HttpClientErrorException) e).getStatusCode().value() == 401) {
//                String message = new AttributedString(
//                        "Unauthorized",
//                        AttributedStyle.DEFAULT.foreground(PromptColor.RED.toJlineAttributedStyle())).toAnsi();
//                return CommandHandlingResult.of(message, 1);
//            }
//            return CommandHandlingResult.of("", 1);
//        }
        return null;
    }
}
