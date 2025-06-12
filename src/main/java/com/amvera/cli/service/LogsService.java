package com.amvera.cli.service;

import com.amvera.cli.client.LogClient;
import com.amvera.cli.dto.project.LogResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class LogsService {
    private final ShellHelper helper;
    private final ProjectService projectService;
    private final LogClient logClient;

    public LogsService(
            ShellHelper helper,
            ProjectService projectService, LogClient logClient
    ) {
        this.helper = helper;
        this.projectService = projectService;
        this.logClient = logClient;
    }

    public void run(String slug, Integer limit, String query, Long last) {
        ProjectResponse project = projectService.findOrSelect(slug);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusMinutes(last);
        List<LogResponse> logList = logClient.run(project.getSlug(), limit, query, start, end);

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
        List<LogResponse> logList = logClient.build(project.getSlug(), limit, query, start, end);

        if (logList.isEmpty()) {
            helper.printWarning("Logs not found. Try again later");
            return;
        }

        logList.forEach(l -> helper.println(l.content()));
    }

}
