package com.amvera.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "endpoints")
public record Endpoints(
        String projects,
        String auth,
        String env,
        String tariff,
        String logs,
        String balance,
        String user,
        String configurations,
        String logout
) {
}
