package com.amvera.cli.exception;

import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

public class CustomExceptionResolver implements CommandExceptionResolver {
    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof CustomException) {
            return CommandHandlingResult.of("Hi, handled exception\n", 42);
        }
        return null;
    }
}
