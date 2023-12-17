package com.amvera.cli.exception;

import com.amvera.cli.utils.ShellHelper;
import org.jline.reader.UserInterruptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;

/**
 * todo: how to turn off logs for native-image? *doesn't catch some exceptions
 * Exception re
 */
public class CustomExceptionResolver implements CommandExceptionResolver {

    @Autowired
    private ShellHelper helper;

    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof CustomException) {
            System.out.println("custom cath: " + e.getMessage());
            return CommandHandlingResult.of("Hi, handled exception\n", 42);
        }
        if (e instanceof ResourceAccessException) {
            if (e.getCause() instanceof ConnectException) {
                helper.printError("Check your internet connection.");
                System.exit(2);
            }
        }
//        if (e instanceof UnauthorizedException) {
//            System.out.println("custom cath: " + e.getMessage());
//            return CommandHandlingResult.of(e.getMessage(), 2);
//        }
        if (e instanceof HttpClientErrorException) {
            if (((HttpClientErrorException) e).getStatusCode().is4xxClientError()) {
                helper.printError("Unauthorized. Wrong credentials.");
            }
            if (((HttpClientErrorException) e).getStatusCode().is5xxServerError()) {
                helper.printError("Server error. Try again later.");
            }
            System.exit(2);
        }
//        if (e instanceof IOException) {
//            return CommandHandlingResult.of(" io exception", 0);
//        }
//        if (e instanceof IllegalStateException) {
//            return CommandHandlingResult.of(" state exception", 0);
//        }
//        if (e instanceof NumberFormatException) {
//            return CommandHandlingResult.of("Format exception", 1);
//        }
//        if (e instanceof UserInterruptException) {
//            return CommandHandlingResult.of("user inter exception", 1);
//        }
//        if (e instanceof CommandNotCurrentlyAvailable) {
//            return CommandHandlingResult.of("CommandNotCurrentlyAvailable", 1);
//        }
//        if (e instanceof RuntimeException) {
//            System.out.println("Catch runtime exception");
//            System.out.println(e.getMessage());
//            System.exit(1);
//            return CommandHandlingResult.of("RuntimeException from resolver ", 1);
//        }
//        if (e instanceof InvocationTargetException) {
//            return CommandHandlingResult.of("InvocationTargetException", 1);
//        }

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
