package com.amvera.cli.exception;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.result.CommandNotFoundMessageProvider;

public class CommandNotFoundMessageProviderCustom implements CommandNotFoundMessageProvider {
    @Override
    public String apply(ProviderContext context) {
        return new AttributedString(
                context.error().getMessage() + ". Use amvera help to list all available commands.",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .toAnsi();
    }
}