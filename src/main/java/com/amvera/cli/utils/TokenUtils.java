package com.amvera.cli.utils;

import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RefreshTokenPostRequest;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.exception.InformException;
import com.amvera.cli.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class TokenUtils {
    private final AppProperties properties;
    private final ObjectMapper mapper;
    private final Endpoints endpoints;
    private final ConfigUtils configUtils;

    public TokenUtils(
            AppProperties properties,
            ObjectMapper mapper,
            Endpoints endpoints,
            ConfigUtils configUtils
    ) {
        this.properties = properties;
        this.mapper = mapper;
        this.endpoints = endpoints;
        this.configUtils = configUtils;
    }

    public void saveToken(String accessToken, String refreshToken) {
        try {
            TokenConfig tokenConfig = new TokenConfig(accessToken, refreshToken);

            if (Files.notExists(FileUtils.AMVERA_DIR)) {
                Files.createDirectories(FileUtils.AMVERA_DIR);

                if (Files.notExists(FileUtils.CONFIG_PATH)) {
                    configUtils.createUserConfig();
                }
            }

            mapper.writeValue(FileUtils.TOKEN_PATH.toFile(), tokenConfig);
        } catch (IOException e) {
            throw new InformException("Unable to save token. Contact us to solve the problem.");
        }
    }

    public TokenConfig readToken() {
        try {
            return mapper.readValue(FileUtils.TOKEN_PATH.toFile(), TokenConfig.class);
        } catch (IOException e) {
            return new TokenConfig("", "");
        }
    }

    public String deleteToken() {
        try {
            File fileToDelete = FileUtils.TOKEN_PATH.toFile();
            fileToDelete.delete();
        } catch (Exception e) {
            throw new InformException("Unable to delete token. Try to delete .amvera.json manually.");
        }

        return "Logged out successfully!";
    }

    public boolean health(String token) {
        try {
            info(token);
            return true;
        } catch (UnauthorizedException e) {
            return false;
        }
    }

//    private TokenConfig refreshToken(String refreshToken) {
//        try {
//            AuthResponse response = refresh(refreshToken);
//
//            if (response == null) {
//                throw new InformException("Unable to refresh tokens. Contact us to solve the problem.");
//            }
//
//            TokenConfig tokenConfig = new TokenConfig(response.getAccessToken(), response.getRefreshToken());
//            mapper.writeValue(new File(PATH), tokenConfig);
//
//            return tokenConfig;
//
//        } catch (IOException e) {
//            throw new InformException("Unable to save token. Contact us to solve the problem.");
//        }
//
//    }

    private void info(String token) {
        client()
                .get().uri("/account")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    if (status == 401 || status == 403) {
                        throw new UnauthorizedException("Try to login again");
                    }
                })
                .toBodilessEntity();
    }

    private AuthResponse refresh(String refreshToken) {
        RefreshTokenPostRequest body = new RefreshTokenPostRequest(properties.keycloakClient(), refreshToken);
        AuthResponse response = client()
                .post().uri("/protocol/openid-connect/token")
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        return response;
    }

    private RestClient client() {
        return RestClient.create(endpoints.keycloak());
    }

}
