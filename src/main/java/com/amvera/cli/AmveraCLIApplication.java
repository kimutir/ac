package com.amvera.cli;

import com.amvera.cli.command.project.ProjectFlows;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.Meta;
import com.amvera.cli.dto.project.config.Toolchain;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
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
@EnableConfigurationProperties({AppProperties.class, Endpoints.class})
@RegisterReflectionForBinding(
        {
                AuthResponse.class,
                AuthRequest.class,
                ProjectResponse.class,
                ProjectListResponse.class,
                ProjectRequest.class,
                ATest.class,
                AmveraCLIApplication.class,
                Meta.class,
                Toolchain.class,
                ProjectFlows.class,
                LogGetResponse.class,
                BalanceGetResponse.class,
                TariffGetResponse.class,
                EnvDTO.class,
                EnvListGetResponse.class,
                EnvPostRequest.class,
                EnvPutRequest.class,
                ScalePostRequest.class
        }
)
public class AmveraCLIApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .sources(AmveraCLIApplication.class)
                .run(args);
    }

    @Bean
    public PromptProvider amveraPromptProvider() {
        return () -> new AttributedString("amvera-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
