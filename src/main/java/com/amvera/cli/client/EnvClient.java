package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.env.EnvPostRequest;
import com.amvera.cli.dto.env.EnvPutRequest;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvClient extends BaseHttpClient {

    public EnvClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.env(), tokenUtils.readToken().accessToken());
    }

    public ResponseEntity<EnvResponse> create(EnvPostRequest req, String slug) {
        ResponseEntity<EnvResponse> response = client()
                .post().uri("/{slug}", slug)
                .body(req)
                .retrieve()
                .toEntity(EnvResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to add environment variables.");
        }

        return response;
    }

    public void update(EnvPutRequest env, String slug) {
        ResponseEntity<EnvResponse> response = client()
                .put().uri("/{slug}/{id}", slug, env.id())
                .body(env)
                .retrieve()
                .toEntity(EnvResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to update environment variables.");
        }
    }

    public void delete(Long id, String slug) {
        ResponseEntity<String> response = client()
                .delete().uri("/{slug}/{id}", slug, id)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to delete environment variables.");
        }
    }

    public List<EnvResponse> get(String slug) {
        ResponseEntity<List<EnvResponse>> envResponse = client()
                .get().uri("/{slug}", slug)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });


        if (envResponse.getStatusCode().isError()) {
            // todo: throw exception
        }

        return envResponse.getBody();
    }

}
