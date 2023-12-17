package com.amvera.cli.exception;

import com.amvera.cli.utils.ShellHelper;
import org.jline.reader.UserInterruptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;

public class CustomExceptionResolver implements CommandExceptionResolver {

    @Autowired
    private ShellHelper helper;

    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof ResourceAccessException) {
            if (e.getCause() instanceof ConnectException) {
                helper.printError("Check your internet connection.");
                System.exit(2);
            }
        }
        if (e instanceof EmptyValueException) {
            helper.printError(e.getMessage());
            System.exit(2);
        }
        if (e instanceof HttpClientErrorException) {
            int code = ((HttpClientErrorException) e).getStatusCode().value();
            if (code == 204) {
                helper.println(e.getMessage());
            }
            if (code == 400) {
                helper.printError("Request exception. Please contact us.");
            }
            if (code == 401) {
                helper.printError("Unauthorized. Wrong credentials.");
            }
            if (code == 404) {
                helper.println(e.getMessage());
            }
            System.exit(2);
        }
        if (e instanceof HttpServerErrorException) {
            helper.printError("Internal server error. Please contact us.");
        }
        // Exception when Ctr + C
        if (e instanceof UserInterruptException) {
            System.exit(2);
        }
        if (e instanceof NullPointerException) {
            helper.printError("Value is null.");
            System.exit(2);
        }
        if (e instanceof RuntimeException) {
            helper.printError(e.getMessage());
            System.exit(2);
        }


        return null;
    }
}
