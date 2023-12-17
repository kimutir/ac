package com.amvera.cli;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.exception.CustomException;
import com.amvera.cli.utils.ShellHelper;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.Shell;
import org.springframework.shell.boot.StandardCommandsAutoConfiguration;
import org.springframework.shell.command.CommandExecution;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.result.ResultHandlerConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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

       }
       catch (IllegalStateException e) {
           System.out.println("catch in try");
           // If command not available
           System.out.println(e.getCause() instanceof CommandNotCurrentlyAvailable);
           // Unrecognized option
           System.out.println(e.getCause() instanceof CommandExecution.CommandParserExceptionsException);
           System.out.println("catch cause: " + e.getCause().getMessage());

       } catch (Exception e) {

       }
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }



}
