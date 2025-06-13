package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.LogResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class LogsService {
    private final ShellHelper helper;
    private final ProjectService projectService;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    public LogsService(
            ShellHelper helper,
            ProjectService projectService,
            Endpoints endpoints,
            AmveraHttpClient client
    ) {
        this.helper = helper;
        this.projectService = projectService;
        this.endpoints = endpoints;
        this.client = client;
    }

    public void run(String slug, Integer limit, String query, Long last) {
        ProjectResponse project = projectService.findOrSelect(slug);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusMinutes(last);

        List<LogResponse> logList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.logs() + "/v2/run/history")
                        .queryParam("serviceName", project.getSlug())
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build()
                        .toUri(),
                new ParameterizedTypeReference<>() {
                },
                "Error when getting run logs"
        );

        if (logList.isEmpty()) {
            helper.printWarning("Logs not found. Try again later");
            return;
        }

        logList.forEach(l -> helper.println(l.content()));
    }

    public void build(String slug, Integer limit, String query, Long last) {
        ProjectResponse project = projectService.findOrSelect(slug, p -> p.getServiceType().equals(ServiceType.PROJECT));

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusMinutes(last);

        List<LogResponse> logList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.logs() + "/v2/build/history")
                        .queryParam("serviceName", project.getSlug())
                        .queryParam("limit", limit)
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build()
                        .toUri(),
                new ParameterizedTypeReference<>() {
                },
                "Error when getting run logs"
        );

        if (logList.isEmpty()) {
            helper.printWarning("Logs not found. Try again later");
            return;
        }

        logList.forEach(l -> helper.println(l.content()));
    }

}
