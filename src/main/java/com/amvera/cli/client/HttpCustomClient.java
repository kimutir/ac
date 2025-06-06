package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.utils.TokenReader;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HttpCustomClient {
    private final Endpoints endpoints;
    private final TokenReader tokenUtils;

    @Autowired
    public HttpCustomClient(
            Endpoints endpoints,
            TokenReader tokenUtils
    ) {
        this.endpoints = endpoints;
        this.tokenUtils = tokenUtils;
    }

    public RestClient.Builder project(String token) {
        return RestClient.builder()
                .baseUrl(endpoints.projects())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public RestClient.Builder project() {
        return RestClient.builder()
                .baseUrl(endpoints.projects())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUtils.readToken().accessToken());
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

    public RestClient marketplace() {
        return RestClient.builder()
                .baseUrl(endpoints.marketplace())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUtils.readToken().accessToken())
                .build();
    }

    public RestClient postgresql() {
        return RestClient.builder()
                .baseUrl(endpoints.postgresql())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUtils.readToken().accessToken())
                .build();
    }
}
