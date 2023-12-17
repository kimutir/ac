package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentService {
    private final HttpCustomClient client;

    public EnvironmentService(
            HttpCustomClient client
    ) {
        this.client = client;
    }

    public List<EnvDTO> getEnvironment(ProjectResponse project) {
//        ProjectResponse projectResponse = projectService.findBy(project);
        String token = TokenUtils.readToken();
        EnvListGetResponse envs = client.environment(token).build()
                .get().uri("/{slug}", project.getSlug())
                .retrieve()
                .body(EnvListGetResponse.class);

        return envs.environmentVariables();
    }

    public void addEnvironment(EnvPostRequest env, String slug) {
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.environment(token).build()
                .post().uri("/{slug}", slug)
                .body(env)
                .retrieve()
                .toEntity(String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public void updateEnvironment(EnvPutRequest env, String slug) {
        String token = TokenUtils.readToken();
        ResponseEntity<String> response = client.environment(token).build()
                .put().uri("/{slug}/{id}", slug, env.id())
                .retrieve()
                .toEntity(String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public void deleteEnvironment(Integer id, String slug) {
        String token = TokenUtils.readToken();
        ResponseEntity<String> response = client.environment(token).build()
                .delete().uri("/{slug}/{id}", slug, id)
                .retrieve()
                .toEntity(String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public List<EnvDTO> getEnvironmentBySlug(String slug) {
        String token = TokenUtils.readToken();
        EnvListGetResponse envs = client.environment(token).build()
                .get().uri("/{slug}", slug)
                .retrieve()
                .body(EnvListGetResponse.class);

        return envs.environmentVariables();
    }

}
