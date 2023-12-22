package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpCustomClient {
    private final Endpoints endpoints;

    public HttpCustomClient(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public RestClient.Builder project(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.projects())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    public RestClient.Builder configurations(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.configurations())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    public RestClient.Builder logs(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.logs())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder balance(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.balance())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder environment(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.env())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder tariff(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.projects())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    public RestClient.Builder auth() {
        return RestClient.builder()
                .baseUrl(endpoints.auth())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED));
    }

    public RestClient.Builder info(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.user())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder logout() {
        return RestClient.builder()
                .baseUrl(endpoints.logout())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED));
    }
}
