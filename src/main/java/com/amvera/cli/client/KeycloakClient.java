package com.amvera.cli.client;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RevokeTokenPostRequest;
import com.amvera.cli.dto.user.InfoResponse;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KeycloakClient extends BaseHttpClient {
    private final AppProperties properties;
    private final TokenUtils tokenUtils;
    private final Endpoints endpoints;

    public KeycloakClient(
            AppProperties properties,
            TokenUtils tokenUtils,
            Endpoints endpoints
    ) {
        super(endpoints.keycloak(), tokenUtils.readToken().accessToken());
        this.endpoints = endpoints;
        this.properties = properties;
        this.tokenUtils = tokenUtils;
    }

    public AuthResponse login(String user, String password) {
        AuthRequest body = new AuthRequest(properties.keycloakClient(), user, password);

        AuthResponse response = client()
                .post()
                .uri("/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        if (response != null) {
            tokenUtils.saveToken(response.getAccessToken(), response.getRefreshToken());
        }

        return response;
    }

    public ResponseEntity<String> logout() {
        TokenConfig tokenConfig = tokenUtils.readToken();
        String refreshToken = tokenConfig.refreshToken();

        RevokeTokenPostRequest body = new RevokeTokenPostRequest(properties.keycloakClient(), refreshToken);

        ResponseEntity<String> response = client()
                .post()
                .uri("/protocol/openid-connect/revoke")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
                .body(body.toMultiValueMap())
                .retrieve().toEntity(String.class);

        return response;
    }

    public InfoResponse info() {
        InfoResponse info = super.client()
                .get().uri("/account")
                .retrieve()
                .body(InfoResponse.class);

        if (info == null) {
            throw new RuntimeException("Unable to get user information.");
        }

        return info;
    }

    public RestClient client() {
       return RestClient.builder()
                .baseUrl(endpoints.keycloak())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

}
