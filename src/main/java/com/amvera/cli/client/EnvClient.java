package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvClient extends BaseHttpClient {

    public EnvClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.env(), tokenUtils.readToken().accessToken());
    }

    public EnvResponse create(EnvPostRequest body, String slug) {
        return client()
                .post().uri("/{slug}", slug)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when adding env to %s", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(EnvResponse.class);
    }

    public void update(EnvPutRequest env, String slug) {
        client()
                .put().uri("/{slug}/{id}", slug, env.id())
                .body(env)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when updating %s env", env.name());
                    throw new ClientResponseException(msg, status);
                });
    }

    public void delete(Long id, String slug) {
        client()
                .delete().uri("/{slug}/{id}", slug, id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when deleting env";
                    throw new ClientResponseException(msg, status);
                });
    }

    public List<EnvResponse> getAll(String slug) {
        return client()
                .get().uri("/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting env list";
                    throw new ClientResponseException(msg, status);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
