package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AppProperties properties;
    private final HttpCustomClient client;
    private final TokenUtils tokenUtils;

    public UserService(
            AppProperties properties,
            HttpCustomClient client,
            TokenUtils tokenUtils
    ) {
        this.properties = properties;
        this.client = client;
        this.tokenUtils = tokenUtils;
    }

    public String login(String user, String password) {
        AuthRequest body = new AuthRequest(properties.keycloakClient(), user, password);

        AuthResponse response = client.auth().build()
                .post()
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        if (response != null) {
            tokenUtils.saveToken(response.getAccessToken(), response.getRefreshToken());
        }

        return "Authorized successfully!";
    }

    public String logout() {
        tokenUtils.deleteToken();
        return "Logged out successfully!";
    }

    public InfoResponse info() {
        String token = tokenUtils.readToken();
        InfoResponse info = client.info(token).build()
                .get().retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

}
