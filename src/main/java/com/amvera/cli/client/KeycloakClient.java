package com.amvera.cli.client;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RefreshTokenPostRequest;
import com.amvera.cli.dto.auth.RevokeTokenPostRequest;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class KeycloakClient extends HttpClientAbs {
    private final AppProperties properties;
    private final TokenUtils tokenUtils;

    public KeycloakClient(
            Endpoints endpoints,
            AppProperties properties,
            TokenUtils tokenUtils
    ) {
        super(endpoints.keycloak(), tokenUtils.readToken().accessToken());
        this.properties = properties;

        this.tokenUtils = tokenUtils;
    }

    public AuthResponse login(String user, String password) {
        AuthRequest body = new AuthRequest(properties.keycloakClient(), user, password);

        AuthResponse response = client()
                .post()
                .uri("/protocol/openid-connect/token")
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        if (response != null) {
            tokenUtils.saveToken(response.getAccessToken(), response.getRefreshToken());
        }

        return response;
    }

    public AuthResponse refresh() {
        RefreshTokenPostRequest body = new RefreshTokenPostRequest(properties.keycloakClient(), tokenUtils.readToken().refreshToken());
        AuthResponse response = client()
                .post().uri("/protocol/openid-connect/token")
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        return response;
    }

    public ResponseEntity<String> logout() {
        TokenConfig tokenConfig = tokenUtils.readToken();
        String refreshToken = tokenConfig.refreshToken();

        RevokeTokenPostRequest body = new RevokeTokenPostRequest(properties.keycloakClient(), refreshToken);

        ResponseEntity<String> response = client()
                .post()
                .uri("/protocol/openid-connect/revoke")
                .body(body.toMultiValueMap())
                .retrieve().toEntity(String.class);

        return response;
    }

    public InfoResponse info() {
        InfoResponse info = client()
                .get().uri("/account")
                .retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

}
