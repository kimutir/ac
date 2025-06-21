package com.amvera.cli;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.jline.PromptProvider;

import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication(proxyBeanMethods = false)
@CommandScan
@EnableAspectJAutoProxy
@EnableConfigurationProperties({AppProperties.class, Endpoints.class})
public class AmveraCLIApplication {
    public static void main(String[] args) {
        try {
            if (args.length == 1 && (Objects.equals(args[0], "-h") || Objects.equals(args[0], "--help"))) {
                args = new String[] { "help" };
            }

            new SpringApplicationBuilder()
                    .web(WebApplicationType.NONE)
                    .sources(AmveraCLIApplication.class)
                    .run(args);

        } catch (IllegalStateException e) {
            System.exit(2);
        } catch (CommandNotCurrentlyAvailable e) {
            String message = e.getMessage();
            System.out.println(message);
            System.exit(2);
        } catch (CommandNotFound e) {
            System.exit(2);
        }
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
