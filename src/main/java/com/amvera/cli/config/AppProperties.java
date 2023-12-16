package com.amvera.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amvera")
public record AppProperties(
        String keycloakClient,
        String version
) {
}
