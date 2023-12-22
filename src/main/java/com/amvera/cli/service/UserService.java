package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class UserService {
    private final AppProperties properties;
    private final HttpCustomClient client;

    public UserService(
            AppProperties properties,
            HttpCustomClient client) {
        this.properties = properties;
        this.client = client;
    }

    public String login(String user, String password) {
        AuthRequest body = new AuthRequest(properties.keycloakClient(), user, password);
        AuthResponse authResponse = client.auth().build()
                .post()
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        if (authResponse != null) {
            TokenUtils.saveToke(authResponse.getAccessToken());
        }

        return "Authorized successfully!";
    }

    public String logout() {
        TokenUtils.deleteToken();

        return "Logged out successfully!";
    }

    public InfoResponse info() {
        String token = TokenUtils.readToken();
        InfoResponse info = client.info(token).build()
                .get().retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

    public int health() {
        try {
            String token = TokenUtils.readToken();
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

//        public Mono<AuthResponse> loginFlux(String user, String password) {
//        StringBuilder stringBuilder = new StringBuilder();
//        AuthRequest body = new AuthRequest(properties.getKeycloakClient(), user, password);
//        return authClient.login(body.toMultiValueMap());
//    }

}
