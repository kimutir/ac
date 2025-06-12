package com.amvera.cli.utils;

import com.amvera.cli.exception.InformException;
import com.amvera.cli.exception.TokenNotFoundException;
import com.amvera.cli.dto.user.TokenConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TokenReader {

    private final ObjectMapper mapper;
//    private final UserService authService;

    // HOME - Mac OS
    // USERPROFILE - Windows
    private static final String HOME = System.getenv("HOME") != null ? System.getenv("HOME") : System.getenv("USERPROFILE");
    private static final String PATH = HOME + File.separator + ".amvera.json";

    public TokenReader(
            ObjectMapper mapper
//            UserService authService
    ) {
        this.mapper = mapper;
//        this.authService = authService;
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
//            int health = authService.health(tokenConfig.accessToken());
            // if not, request new access and refresh tokens
//            if (health != 200) {
//                try {
//                    AuthResponse response = authService.refreshToken(tokenConfig.refreshToken());
//
//                    if (response == null) {
//                        throw new InformException("Unable to refresh tokens. Contact us to solve the problem.");
//                    }
//
//                    tokenConfig = new TokenConfig(response.getAccessToken(), response.getRefreshToken());
//                    mapper.writeValue(new File(PATH), tokenConfig);
//
//                    return tokenConfig;
//
//                } catch (IOException e) {
//                    throw new InformException("Unable to save token. Contact us to solve the problem.");
//                }
//            }

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

}
