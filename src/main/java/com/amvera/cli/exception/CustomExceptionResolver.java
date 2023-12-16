package com.amvera.cli.exception;

import org.jline.reader.UserInterruptException;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * todo: how to turn off logs for native-image? *doesn't catch some exceptions
 * Exception re
 */
public class CustomExceptionResolver implements CommandExceptionResolver {

    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof CustomException) {
            return CommandHandlingResult.of("Hi, handled exception\n", 42);
        }
        if (e instanceof InterruptedIOException) {
            return CommandHandlingResult.of("iter exception", 0);
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
        if (e instanceof UserInterruptException) {
            return CommandHandlingResult.of("user inter exception", 1);
        }
        if (e instanceof CommandNotCurrentlyAvailable) {
            return CommandHandlingResult.of("unavai", 1);
        }

        System.out.println("SOMETHING HAPPENED");

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
