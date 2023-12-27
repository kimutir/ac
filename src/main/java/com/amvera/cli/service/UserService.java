package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RevokeTokenPostRequest;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class UserService {
    private final TokenUtils tokenUtils;
    private final AppProperties properties;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    public UserService(
            TokenUtils tokenUtils,
            AppProperties properties,
            Endpoints endpoints,
            AmveraHttpClient client
    ) {
        this.tokenUtils = tokenUtils;
        this.properties = properties;
        this.endpoints = endpoints;
        this.client = client;
    }

    public String login(String user, String password) {
        AuthResponse response = client.post(
                URI.create(endpoints.keycloak() + "/protocol/openid-connect/token"),
                AuthResponse.class,
                new AuthRequest(properties.keycloakClient(), user, password).toMultiValueMap(),
                h -> h.setContentType(MediaType.APPLICATION_FORM_URLENCODED),
                "Error on login"
        );

        if (response != null) {
            tokenUtils.saveToken(response.getAccessToken(), response.getRefreshToken());
        }

        return "Authorized successfully!";
    }

    public String logout() {
        TokenConfig tokenConfig = tokenUtils.readToken();
        String refreshToken = tokenConfig.refreshToken();

        client.post(
                URI.create(endpoints.keycloak() + "/protocol/openid-connect/revoke"),
                "Error on logout",
                new RevokeTokenPostRequest(properties.keycloakClient(), refreshToken).toMultiValueMap(),
                h -> {
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    h.setBearerAuth(tokenConfig.accessToken());
                });

        return tokenUtils.deleteToken();
    }

    public InfoResponse info() {
        return client.get(
                URI.create(endpoints.keycloak() + "/account"),
                InfoResponse.class,
                "Error on getting user info"
        );
    }

}
