package com.amvera.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "endpoints")
public record Endpoints(
        String projects,
        String env,
        String tariff,
        String logs,
        String balance,
        String domain,
        String keycloak,
        String configurations,
        String marketplace,
        String postgresql
) {
}
