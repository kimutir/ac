package com.amvera.cli.config;

import com.amvera.cli.exception.CustomExceptionResolver;
import com.amvera.cli.utils.ShellHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ShellHelper shellHelper(Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    CustomExceptionResolver customExceptionResolver() {
        return new CustomExceptionResolver();
    }

//    @Bean
//    ObjectMapper objectMapper() {
//        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }
}
