package com.amvera.cli.client;

import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

public abstract class BaseHttpClient {

    private final String token;

    public BaseHttpClient(String token) {
        this.token = "Bearer " + token;
    }

    public RestClient client(Consumer<HttpHeaders> headers, String err) {
        return RestClient.builder()
                .defaultHeaders(headers)
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();

                    if (status == 401 || status == 403) throw new UnauthorizedException("Try to login again");

                    throw new ClientResponseException(err, HttpStatus.valueOf(status));
                })
                .build();
    }

    public RestClient client(String err) {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, this.token)
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();

                    System.out.println(status);

                    if (status == 401 || status == 403) throw new UnauthorizedException("Try to login again");

                    throw new ClientResponseException(err, HttpStatus.valueOf(status));
                })
                .build();
    }

}
