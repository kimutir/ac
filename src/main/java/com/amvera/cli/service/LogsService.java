package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.project.LogResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class LogsService {
    private final AmveraHttpClient client;
    private final ObjectMapper mapper;
    private final TokenUtils tokenUtils;
    private final ShellHelper helper;
    private final ProjectService projectService;

    public LogsService(
            AmveraHttpClient client,
            ObjectMapper mapper,
            TokenUtils tokenUtils,
            ShellHelper helper,
            ProjectService projectService
    ) {
        this.client = client;
        this.mapper = mapper;
        this.tokenUtils = tokenUtils;
        this.helper = helper;
        this.projectService = projectService;
    }

    public void run(String slug, Integer limit, String query, Long last) {
        ProjectResponse project = projectService.findOrSelect(slug);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusMinutes(last);
        List<LogResponse> logList = runLogsRequest(project.getSlug(), limit, query, start, end);

        if (logList.isEmpty()) {
            helper.printWarning("Logs not found. Try again later");
            return;
        }

        logList.forEach(l -> helper.println(l.content()));
    }

    public void build(String slug, Integer limit, String query, Long last) {
        ProjectResponse project = projectService.findOrSelect(slug);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusMinutes(last);
        List<LogResponse> logList = buildLongsRequest(project.getSlug(), limit, query, start, end);

        if (logList.isEmpty()) {
            helper.printWarning("Logs not found. Try again later");
            return;
        }

        logList.forEach(l -> helper.println(l.content()));
    }

    public List<LogResponse> buildLongsRequest(String slug, Integer limit, String query, OffsetDateTime start, OffsetDateTime end) {
        ResponseEntity<List<LogResponse>> response = client.logs().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/build/history")
                        .queryParam("serviceName", slug)
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public List<LogResponse> runLogsRequest(String slug, Integer limit, String query, OffsetDateTime start, OffsetDateTime end) {
        ResponseEntity<List<LogResponse>> response = client.logs().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/run/history")
                        .queryParam("serviceName", slug)
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public List<LogResponse> logs(ProjectResponse project, String type, int limit) {
        String token = tokenUtils.readToken().accessToken();
        ResponseEntity<String> response = client.logs(token).build().get()
                .uri("/{type}/history?username={user}&serviceName={name}&limit={limit}", type, project.getOwnerName(), project.getSlug(), limit)
                .retrieve()
                .toEntity(String.class);

        List<LogResponse> logs;

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
