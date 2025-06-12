package com.amvera.cli.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public abstract class HttpClientAbs {

    private final String url;
    private final String token;
    private final MediaType contentType;

    public HttpClientAbs(String url, String token) {
        this.url = url;
        this.token = "Bearer " + token;
        this.contentType = MediaType.APPLICATION_JSON;
    }

    public HttpClientAbs(String url, String token, MediaType contentType) {
        this.url = url;
        this.token = "Bearer " + token;
    }

    public RestClient client() {
        return RestClient.builder()
                .baseUrl(this.url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, this.token)
                .build();
    }
}
