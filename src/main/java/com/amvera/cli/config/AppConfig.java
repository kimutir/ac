package com.amvera.cli.config;

import com.amvera.cli.AmveraCLIApplication;
import com.amvera.cli.command.project.ProjectFlows;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.dto.project.config.Meta;
import com.amvera.cli.dto.project.config.Toolchain;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.exception.CustomExceptionResolver;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.model.TariffTableModel;
import com.amvera.cli.service.UserService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.terminal.Terminal;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;

@Configuration
@RegisterReflectionForBinding(
        {
                AuthResponse.class,
                AuthRequest.class,
                ProjectGetResponse.class,
                ProjectListResponse.class,
                ProjectRequest.class,
                ProjectPostResponse.class,
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
                ScalePostRequest.class,
                ProjectTableModel.class,
                TariffTableModel.class,
                InfoResponse.class,
                AmveraConfiguration.class
        }
)
public class AppConfig {
    @Bean
    public ShellHelper shellHelper(Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public Boolean isValid(UserService authService) {
        return authService.health() == 200;
    }

    @Bean
    public AvailabilityProvider userLoggedProvider(Boolean isValid) {
        return () -> !isValid
                ? Availability.available()
                : Availability.unavailable("you are already logged in.");
    }

    @Bean
    public AvailabilityProvider userLoggedOutProvider(Boolean isValid) {
        return  () -> isValid
                ? Availability.available()
                : Availability.unavailable("you are not logged in.");
    }

    @Bean
    public CustomExceptionResolver resolver() {
        return new CustomExceptionResolver();
    }

}
