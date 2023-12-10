package com.amvera.cli.service;

import com.amvera.cli.client.KeycloakAuthClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final KeycloakAuthClient authClient;
    private final AppProperties properties;
    private final Terminal terminal;

    public AuthService(KeycloakAuthClient authClient, AppProperties properties, Terminal terminal) {
        this.authClient = authClient;
        this.properties = properties;
        this.terminal = terminal;
    }

    public Mono<AuthResponse> login(String user, String password) {
        StringBuilder stringBuilder = new StringBuilder();
        AuthRequest body = new AuthRequest(properties.getKeycloakClient(), user, password);
        return authClient.login(body.toMultiValueMap());
    }
}
