package com.amvera.cli.service;

import com.amvera.cli.client.KeycloakAuthClient;
import com.amvera.cli.config.AppProperties;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.terminal.Terminal;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final KeycloakAuthClient authClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final AppProperties properties;
    private final Endpoints endpoints;

    public AuthService(KeycloakAuthClient authClient, RestTemplate restTemplate, ObjectMapper mapper, AppProperties properties, Endpoints endpoints) {
        this.authClient = authClient;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.properties = properties;
        this.endpoints = endpoints;
    }

    public Mono<AuthResponse> loginFlux(String user, String password) {
        StringBuilder stringBuilder = new StringBuilder();
        AuthRequest body = new AuthRequest(properties.getKeycloakClient(), user, password);
        return authClient.login(body.toMultiValueMap());
    }

    public String login(String user, String password) {
        String url = "https://id.amvera.ru/auth/realms/amvera/protocol/openid-connect/token";
//        String url = "https://id.staging.amvera.ru/auth/realms/amvera-staging/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        AuthRequest body = new AuthRequest(properties.getKeycloakClient(), user, password);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body.toMultiValueMap(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        AuthResponse authResponse = null;
        try {
            authResponse = mapper.readValue(response.getBody(), AuthResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        TokenUtils.saveResponseToken(authResponse.getAccessToken());

        return authResponse.getAccessToken();
    }

    public void info() {
        String token = TokenUtils.readResponseToken();
        HttpHeaders projectsHeaders = new HttpHeaders();
        projectsHeaders.setContentType(MediaType.APPLICATION_JSON);
        projectsHeaders.setBearerAuth(token);
        HttpEntity<Object> projectsEntity = new HttpEntity<>(projectsHeaders);
        ResponseEntity<String> response = restTemplate.exchange(endpoints.user(), HttpMethod.GET, projectsEntity, String.class);
        System.out.println(response.getBody());

    }
}
