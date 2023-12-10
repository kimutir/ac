package com.amvera.cli.config;

import com.amvera.cli.client.KeycloakAuthClient;
import com.amvera.cli.exception.CustomExceptionResolver;
import com.amvera.cli.utils.ShellHelper;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory() {
        WebClient client = WebClient.builder()
                .baseUrl("https://id.amvera.ru")

                .build();
        return HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(client)
        ).build();
    }

    @Bean
    public KeycloakAuthClient authClient(HttpServiceProxyFactory factory) {
        return factory.createClient(KeycloakAuthClient.class);
    }

    @Bean
    public ShellHelper shellHelper(Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    CustomExceptionResolver customExceptionResolver() {
        return new CustomExceptionResolver();
    }
}
