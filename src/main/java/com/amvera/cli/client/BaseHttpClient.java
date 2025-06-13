package com.amvera.cli.client;

import com.amvera.cli.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
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
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();

                    if (status == 401 || status == 403) {
                        throw new UnauthorizedException("Try to login again");
                    }
                })
                .build();
    }
}
