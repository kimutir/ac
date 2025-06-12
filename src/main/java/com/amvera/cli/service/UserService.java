package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RefreshTokenPostRequest;
import com.amvera.cli.dto.auth.RevokeTokenPostRequest;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.exception.InformException;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class UserService {
    private final AppProperties properties;
    private final AmveraHttpClient client;
    private final TokenUtils tokenUtils;

    public UserService(
            AppProperties properties,
            AmveraHttpClient client,
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

        return tokenUtils.deleteToken();
    }

    public InfoResponse info() {
        String token = tokenUtils.readToken().accessToken();

        InfoResponse info = client.info(token).build()
                .get().retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

    public int health(String token) {
        try {
            ResponseEntity<String> response = client.info(token).build()
                    .get().retrieve()
                    .toEntity(String.class);
            return response.getStatusCode().value();
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value();
        } catch (Exception e) {
            return 0;
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        RefreshTokenPostRequest body = new RefreshTokenPostRequest(properties.keycloakClient(), refreshToken);
        try {
            AuthResponse response = client.auth().build()
                    .post()
                    .body(body.toMultiValueMap())
                    .retrieve().body(AuthResponse.class);

            if (response == null) {
                throw new InformException("Unable to refresh tokens. Contact us to solve the problem.");
            }

            return response;

        } catch (Exception e) {
            throw new InformException("Unable to save token. Contact us to solve the problem.");
        }

    }

}
