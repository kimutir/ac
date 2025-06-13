package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class AmveraHttpClient extends BaseHttpClient {
    private final Endpoints endpoints;

    public AmveraHttpClient(TokenUtils tokenUtils, Endpoints endpoints1) {
        super(tokenUtils.readToken().accessToken());
        this.endpoints = endpoints1;
    }

    public <T> T project(HttpMethod method, URI uri, Class<T> clazz, String err) {
        return super.client(endpoints.projects(), err)
                .method(method)
                .uri(uri)
                .retrieve()
                .body(clazz);
    }

    public <T> T env(HttpMethod method, URI uri, Class<T> clazz, String err) {
        return super.client(endpoints.env(), err)
                .method(method)
                .uri(uri)
                .retrieve()
                .body(clazz);
    }

    public <T> T tariff(HttpMethod method, URI uri, Class<T> clazz, String err) {
        return super.client(endpoints.tariff(), err)
                .method(method)
                .uri(uri)
                .retrieve()
                .body(clazz);
    }


}
