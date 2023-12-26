package com.amvera.cli.utils;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.auth.RefreshTokenRequest;
import com.amvera.cli.model.TokenConfig;
import com.amvera.cli.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;

@Component
public class TokenUtils {
    private final ObjectMapper mapper;
    private final HttpCustomClient client;
    private final AppProperties properties;

    // HOME - Mac OS
    // USERPROFILE - Windows
    private static final String HOME = System.getenv("HOME") != null ? System.getenv("HOME") : System.getenv("USERPROFILE");
    private static final String PATH = HOME + File.separator + ".amvera.json";

    public TokenUtils(
            ObjectMapper mapper,
            HttpCustomClient client,
            AppProperties properties
    ) {
        this.mapper = mapper;
        this.client = client;
        this.properties = properties;
    }

    public void saveToken(String accessToken, String refreshToken) {
        TokenConfig tokenConfig = new TokenConfig(accessToken, refreshToken);
        try {
            mapper.writeValue(new File(PATH), tokenConfig);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save token. Contact us to solve problem.");
        }
    }

    public String readToken() {
        try {
            String token;
            TokenConfig tokenConfig = mapper.readValue(new File(PATH), TokenConfig.class);
            int health = health(tokenConfig.accessToken());

            if (health != 200) {
                tokenConfig = refreshToken(tokenConfig.refreshToken());
            }

            token = tokenConfig.accessToken();
            return token;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        String token;
//        try (FileInputStream fileInputStream = new FileInputStream(PATH)) {
//            token = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
//            return token;
//        }
//        catch (IOException e) {
//            throw new TokenNotFoundException("Token was not found.");
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Unable to read token. Contact us to solve problem.");
//        }
    }

    public String deleteToken() {
        File fileToDelete = new File(PATH);
        fileToDelete.delete();

        return "Logged out successfully!";
    }

    private int health(String token) {
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

    private TokenConfig refreshToken(String refreshToken) {
        RefreshTokenRequest body = new RefreshTokenRequest(properties.keycloakClient(), refreshToken);

        AuthResponse response = client.auth().build()
                .post()
                .body(body.toMultiValueMap())
                .retrieve().body(AuthResponse.class);

        System.out.println("refresh response: " + response.getAccessToken());

        TokenConfig tokenConfig = new TokenConfig(response.getAccessToken(), response.getRefreshToken());

        try {
            mapper.writeValue(new File(PATH), tokenConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tokenConfig;
    }

}
