package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AmveraHttpClient {
    private final Endpoints endpoints;

    @Autowired
    public AmveraHttpClient(
            Endpoints endpoints
    ) {
        this.endpoints = endpoints;
    }

    public RestClient.Builder auth() {
        return RestClient.builder()
                .baseUrl(endpoints.token())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED));
    }

    public RestClient.Builder info(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.user())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder logout(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.logout())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
