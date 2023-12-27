package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RevokeTokenPostRequest;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.model.TokenConfig;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
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
        TokenConfig tokenConfig = tokenUtils.readToken();
        String refreshToken = tokenConfig.refreshToken();
        String accessToken = tokenConfig.accessToken();

        RevokeTokenPostRequest body = new RevokeTokenPostRequest(properties.keycloakClient(), refreshToken);

        ResponseEntity<String> response = client.logout(accessToken).build()
                .post()
                .body(body.toMultiValueMap())
                .retrieve().toEntity(String.class);

        int code = response.getStatusCode().value();

        System.out.println("Revoke code: " + code);

        return tokenUtils.deleteToken();
    }

    public InfoResponse info() {
        String token = tokenUtils.readToken().accessToken();

        System.out.println("Access token from userinfo request: " + token);

        InfoResponse info = client.info(token).build()
                .get().retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

}
