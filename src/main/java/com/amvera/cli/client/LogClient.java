package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.LogResponse;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class LogClient extends BaseHttpClient {

    public LogClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.logs(), tokenUtils.readToken().accessToken());
    }

    public List<LogResponse> run(String slug, Integer limit, String query, OffsetDateTime start, OffsetDateTime end) {
        ResponseEntity<List<LogResponse>> response = client().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/run/history")
                        .queryParam("serviceName", slug)
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting run logs";
                    throw new ClientResponseException(msg, status);
                })
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public List<LogResponse> build(String slug, Integer limit, String query, OffsetDateTime start, OffsetDateTime end) {
        ResponseEntity<List<LogResponse>> response = client().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/build/history")
                        .queryParam("serviceName", slug)
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting build logs";
                    throw new ClientResponseException(msg, status);
                })
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

}
