package com.amvera.cli.config;

import com.amvera.cli.AmveraCLIApplication;
import com.amvera.cli.dto.auth.*;
import com.amvera.cli.dto.billing.BalanceResponse;
import com.amvera.cli.dto.billing.TariffListResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.dto.domain.IngressPort;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.dto.github.GithubRelease;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.cnpg.*;
import com.amvera.cli.dto.project.config.*;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.dto.user.UserConfig;
import com.amvera.cli.exception.CustomExceptionResolver;
import com.amvera.cli.exception.CommandNotFoundMessageProviderCustom;
import com.amvera.cli.utils.table.CnpgTableModel;
import com.amvera.cli.utils.table.MarketplaceTableModel;
import com.amvera.cli.utils.table.ProjectTableModel;
import com.amvera.cli.utils.table.TariffTableModel;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.TokenUtils;
import org.jline.terminal.Terminal;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;
import org.springframework.shell.result.CommandNotFoundMessageProvider;

@Configuration
@RegisterReflectionForBinding(
        {
                AuthResponse.class,
                AuthRequest.class,
                AmveraConfiguration.class,
                AmveraCLIApplication.class,
                BalanceResponse.class,
                CnpgTableModel.class,
                CnpgBackupPostRequest.class,
                CnpgBackupResponse.class,
                CnpgPostRequest.class,
                CnpgPutRequest.class,
                CnpgResponse.class,
                CnpgRestorePostRequest.class,
                CnpgRestoreResponse.class,
                ConfigResponse.class,
                DefaultConfValuesResponse.class,
                DomainResponse.class,
                EnvResponse.class,
                EnvPostRequest.class,
                EnvPutRequest.class,
                GithubRelease.class,
                InfoResponse.class,
                IngressPort.class,
                LogoutRequest.class,
                LogResponse.class,
                MarketplaceTableModel.class,
                MarketplaceConfig.class,
                MarketplaceConfigPostRequest.class,
                MarketplaceConfigResponse.class,
                Meta.class,
                ProjectResponse.class,
                ProjectListResponse.class,
                ProjectRequest.class,
                ProjectPostResponse.class,
                ProjectTableModel.class,
                RefreshTokenPostRequest.class,
                RevokeTokenPostRequest.class,
                ScalePostRequest.class,
                Toolchain.class,
                TariffResponse.class,
                TariffListResponse.class,
                TariffTableModel.class,
                TokenConfig.class,
                UserConfig.class
        }
)
public class AppConfig {
    @Bean
    public ShellHelper shellHelper(Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public Boolean isValid(TokenUtils tokenUtils) {
        return tokenUtils.health(tokenUtils.readToken().accessToken());
    }

    @Bean
    public AvailabilityProvider userLoggedOutProvider(Boolean isValid) {
        return () -> !isValid
                ? Availability.available()
                : Availability.unavailable("you are already logged in.");
    }

    @Bean
    public AvailabilityProvider userLoggedInProvider(Boolean isValid) {
        return  () -> isValid
                ? Availability.available()
                : Availability.unavailable("you are not logged in.");
    }

    @Bean
    public CustomExceptionResolver resolver() {
        return new CustomExceptionResolver();
    }

    @Bean
    CommandNotFoundMessageProvider notFoundMessageProvider() {
        return new CommandNotFoundMessageProviderCustom();
    }

}
