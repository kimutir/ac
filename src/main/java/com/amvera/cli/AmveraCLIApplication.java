package com.amvera.cli;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@CommandScan
@EnableAspectJAutoProxy
@EnableConfigurationProperties({AppProperties.class, Endpoints.class})
public class AmveraCLIApplication {
    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder()
                    .web(WebApplicationType.NONE)
                    .sources(AmveraCLIApplication.class)
                    .run(args);
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof CommandNotCurrentlyAvailable) {
                // color removed because of incorrect output in PowerShell (windows)
//                String message = new AttributedString(e.getCause().getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
                String message = e.getCause().getMessage();
                System.out.println(message);
            }
        }
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
