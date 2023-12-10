package com.amvera.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amvera")
public class AppProperties {
    private String keycloakClient;

    public String getKeycloakClient() {
        return keycloakClient;
    }

    public void setKeycloakClient(String keycloakClient) {
        this.keycloakClient = keycloakClient;
    }
}
