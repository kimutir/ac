package com.amvera.cli.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public abstract class BaseHttpClient {

    private final String url;
    private final String token;

    public BaseHttpClient(String url, String token) {
        this.url = url;
        this.token = "Bearer " + token;
    }

    public RestClient client() {
        return RestClient.builder()
                .baseUrl(this.url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, this.token)
                .build();
    }
}
