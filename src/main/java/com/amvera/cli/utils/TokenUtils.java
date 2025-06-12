package com.amvera.cli.utils;

import com.amvera.cli.client.KeycloakClient;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.user.TokenConfig;
import com.amvera.cli.exception.InformException;
import com.amvera.cli.exception.TokenNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;

@Component
public class TokenUtils {
    private final ObjectMapper mapper;
    private final KeycloakClient keycloakClient;

    // HOME - Mac OS
    // USERPROFILE - Windows
    private static final String HOME = System.getenv("HOME") != null ? System.getenv("HOME") : System.getenv("USERPROFILE");
    private static final String PATH = HOME + File.separator + ".amvera.json";

    public TokenUtils(
            ObjectMapper mapper,
            KeycloakClient keycloakClient
    ) {
        this.mapper = mapper;
        this.keycloakClient = keycloakClient;
    }

    public void saveToken(String accessToken, String refreshToken) {
        TokenConfig tokenConfig = new TokenConfig(accessToken, refreshToken);
        try {
            mapper.writeValue(new File(PATH), tokenConfig);
        } catch (IOException e) {
            throw new InformException("Unable to save token. Contact us to solve the problem.");
        }
    }

    public TokenConfig readToken() {
        try {
            TokenConfig tokenConfig = mapper.readValue(new File(PATH), TokenConfig.class);
            // check if access token is valid
            int health = health(tokenConfig.accessToken());
            // if not, request new access and refresh tokens
            if (health != 200) {
                tokenConfig = refreshToken(tokenConfig.refreshToken());
            }

            return tokenConfig;

        } catch (IOException e) {
            throw new TokenNotFoundException("Token was not found.");
        }
    }

    public String deleteToken() {
        try {
            File fileToDelete = new File(PATH);
            fileToDelete.delete();
        } catch (Exception e) {
//            System.out.println("Try to delete .amvera.json manually.");
        }

        return "Logged out successfully!";
    }

    private int health(String token) {
        try {
            keycloakClient.info();
            return HttpStatus.OK.value();
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value();
        } catch (Exception e) {
            return 0;
        }
    }

    private TokenConfig refreshToken(String refreshToken) {
        try {
            AuthResponse response = keycloakClient.refresh();

            if (response == null) {
                throw new InformException("Unable to refresh tokens. Contact us to solve the problem.");
            }

            TokenConfig tokenConfig = new TokenConfig(response.getAccessToken(), response.getRefreshToken());
            mapper.writeValue(new File(PATH), tokenConfig);

            return tokenConfig;

        } catch (IOException e) {
            throw new InformException("Unable to save token. Contact us to solve the problem.");
        }

    }

}
