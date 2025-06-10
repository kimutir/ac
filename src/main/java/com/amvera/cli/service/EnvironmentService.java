package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentService {
    private final HttpCustomClient client;
    private final ShellHelper helper;
    private final AmveraTable table;

    public EnvironmentService(
            HttpCustomClient client,
            ShellHelper helper,
            AmveraTable table
    ) {
        this.client = client;
        this.helper = helper;
        this.table = table;
    }

    public void renderTable(String slug) {
        List<EnvDTO> envList = getEnvironmentBySlug(slug);

        if (envList.isEmpty()) {
            helper.printWarning("No environments found. You can add environment by 'amvera create env'\n");
        } else {
            helper.print(table.environments(envList));
        }
    }

    public List<EnvDTO> getEnvironment(ProjectGetResponse project) {
        EnvListGetResponse envs = client.environment().build()
                .get().uri("/{slug}", project.getSlug())
                .retrieve()
                .body(EnvListGetResponse.class);

        if (envs == null) {
            throw ClientExceptions.noContent("Environments variables were not found.");
        }

        return envs.environmentVariables();
    }

    public void addEnvironment(EnvPostRequest env, String slug) {
        ResponseEntity<String> response = client.environment().build()
                .post().uri("/{slug}", slug)
                .body(env)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to add environment variables.");
        }
    }

    public void updateEnvironment(EnvPutRequest env, String slug) {
        ResponseEntity<String> response = client.environment().build()
                .put().uri("/{slug}/{id}", slug, env.id())
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to update environment variables.");
        }
    }

    public void deleteEnvironment(Integer id, String slug) {
        ResponseEntity<String> response = client.environment().build()
                .delete().uri("/{slug}/{id}", slug, id)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Unable to delete environment variables.");
        }
    }

    public List<EnvDTO> getEnvironmentBySlug(String slug) {
        ResponseEntity<List<EnvDTO>> envResponse = client.environment().build()
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
