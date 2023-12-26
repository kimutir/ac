package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.LogGetResponse;
import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogsService {
    private final HttpCustomClient client;
    private final ObjectMapper mapper;
    private final TokenUtils tokenUtils;

    public LogsService(
            HttpCustomClient client,
            ObjectMapper mapper,
            TokenUtils tokenUtils) {
        this.client = client;
        this.mapper = mapper;
        this.tokenUtils = tokenUtils;
    }

    public List<LogGetResponse> logs(ProjectGetResponse project, String type, int limit) {
        String token = tokenUtils.readToken();
        ResponseEntity<String> response = client.logs(token).build().get()
                .uri("/{type}/history?username={user}&serviceName={name}&limit={limit}", type, project.getOwnerName(), project.getSlug(), limit)
                .retrieve()
                .toEntity(String.class);

        List<LogGetResponse> logs;

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Logs loading failed.");
        }

        try {
            logs = mapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Logs loading failed.");
        }

        return logs;
    }

}
