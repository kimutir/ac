package com.example.springshell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.jline.NonInteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@CommandScan
public class SpringShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringShellApplication.class, args);
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
