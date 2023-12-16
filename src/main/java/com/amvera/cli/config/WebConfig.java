package com.amvera.cli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public HttpServiceProxyFactory httpServiceProxyFactory() {
//        WebClient client = WebClient.builder()
//                .baseUrl("https://id.amvera.ru")
//
//                .build();
//        return HttpServiceProxyFactory.builderFor(
//                WebClientAdapter.create(client)
//        ).build();
//    }

//    @Bean
//    public KeycloakAuthClient authClient(HttpServiceProxyFactory factory) {
//        return factory.createClient(KeycloakAuthClient.class);
//    }

}
