package com.amvera.cli.exception;

import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

import java.io.IOException;

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
        if (e instanceof IOException) {
            return CommandHandlingResult.of(" io exception", 0);
        }
        return CommandHandlingResult.of("end exception", 0);
    }
}
