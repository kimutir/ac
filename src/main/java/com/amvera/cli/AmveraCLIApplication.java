package com.amvera.cli;

import com.amvera.cli.config.AppProperties;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@CommandScan
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AppProperties.class)
//@RegisterReflectionForBinding({TokenResponse.class, ProjectResponse.class, ProjectListResponse.class})
public class AmveraCLIApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .sources(AmveraCLIApplication.class)
                        .run(args);
//        SpringApplication.run(AmveraCLIApplication.class, args);
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
