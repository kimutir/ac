package com.amvera.cli;

import com.amvera.cli.dto.ProjectListResponse;
import com.amvera.cli.dto.ProjectResponse;
import com.amvera.cli.dto.TokenResponse;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.shell.boot.StandardCommandsAutoConfiguration;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@CommandScan
@EnableAspectJAutoProxy
//@RegisterReflectionForBinding({TokenResponse.class, ProjectResponse.class, ProjectListResponse.class})
public class AmveraCLIApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmveraCLIApplication.class, args);
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
