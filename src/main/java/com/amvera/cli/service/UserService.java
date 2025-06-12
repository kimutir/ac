package com.amvera.cli.service;

import com.amvera.cli.client.KeycloakClient;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final TokenUtils tokenUtils;
    private final KeycloakClient keycloakClient;

    public UserService(
            TokenUtils tokenUtils,
            KeycloakClient keycloakClient
    ) {
        this.tokenUtils = tokenUtils;
        this.keycloakClient = keycloakClient;
    }

    public String login(String user, String password) {
        AuthResponse response = keycloakClient.login(user, password);

        if (response != null) {
            tokenUtils.saveToken(response.getAccessToken(), response.getRefreshToken());
        }

        return "Authorized successfully!";
    }

    public String logout() {
        ResponseEntity<String> response = keycloakClient.logout();

        return tokenUtils.deleteToken();
    }

    public InfoResponse info() {
        InfoResponse info = keycloakClient.info();

        return info;
    }

}
