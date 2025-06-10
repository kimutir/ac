package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.dto.project.config.ConfigGetResponse;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.model.ProjectTableModel;
import com.amvera.cli.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final HttpCustomClient client;
    private final TokenUtils tokenUtils;
    private final CnpgService cnpgService;
    private final AmveraTable table;
    private final TariffService tariffService;
    private final ShellHelper helper;

    @Autowired
    public ProjectService(
            HttpCustomClient client,
            TokenUtils tokenUtils,
            CnpgService cnpgService,
            AmveraTable table,
            TariffService tariffService,
            ShellHelper helper
    ) {
        this.client = client;
        this.tokenUtils = tokenUtils;
        this.cnpgService = cnpgService;
        this.table = table;
        this.tariffService = tariffService;
        this.helper = helper;
    }

    public void renderTable(String id) {
        ProjectGetResponse project = findBy(id);
        TariffGetResponse tariff = tariffService.getTariff(project.getSlug());
        helper.println(table.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderTable(ProjectGetResponse project) {
        TariffGetResponse tariff = tariffService.getTariff(project.getSlug());
        helper.print(table.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff.id()))));
    }

    public List<ProjectGetResponse> getProjects() {
        ProjectListResponse projectList = client.project().build().get()
                .retrieve()
                .body(ProjectListResponse.class);

        if (projectList == null || projectList.getServices().isEmpty()) {
            throw ClientExceptions.noContent("Projects were not found.");
        }

        return projectList.getServices();
    }

    public ResponseEntity<ConfigGetResponse> getConfig() {
        String token = tokenUtils.readToken().accessToken();

        return client.configurations(token).build().get()
                .retrieve()
                .toEntity(ConfigGetResponse.class);
    }

    public ProjectPostResponse createProject(String name, Integer tariff) {
        ProjectPostResponse project = client.project().build().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .body(ProjectPostResponse.class);

        if (project == null) {
            throw new RuntimeException("Project creation failed.");
        }

        return project;
    }

    public void addConfig(AmveraConfiguration body, String slug) {
        String token = tokenUtils.readToken().accessToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/config?slug={slug}", slug, slug)
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Creating configuration failed.");
        }

        System.out.println("Config amvera.yml added");
    }

    public String rebuild(String p) {
        ProjectGetResponse project = findBy(p);

        ResponseEntity<String> response = client.project().build().post()
                .uri("/{slug}/rebuild", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Rebuilding failed.");
        }

        return "Project rebuilding started...";
    }

    public String restart(String p) {
        ProjectGetResponse project = findBy(p);

        ResponseEntity<String> response = client.project().build().post()
                .uri("/{slug}/restart", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Restarting failed.");
        }

        return "Project restarting started...";
    }

    public ResponseEntity<Void> delete(String p) {
        ProjectGetResponse project = findBy(p);

        ResponseEntity<Void> response = switch (project.getServiceType()) {
            case ServiceType.PROJECT, ServiceType.PRECONFIGURED -> deleteBySlug(project.getSlug());
            case ServiceType.POSTGRESQL -> cnpgService.delete(project.getSlug());
        };

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Deletion failed.");
        }

        return response;
    }


    public ResponseEntity<Void> deleteBySlug(String slug) {
        String token = tokenUtils.readToken().accessToken();

        return client.project(token).build().delete()
                .uri("/{slug}", slug)
                .retrieve().toBodilessEntity();
    }

    public String start(String p) {
        ProjectGetResponse project = findBy(p);
        String token = tokenUtils.readToken().accessToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(1))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to start project.");
        }

        return "Project started!";
    }

    public String stop(String p) {
        ProjectGetResponse project = findBy(p);
        String token = tokenUtils.readToken().accessToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(0))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to stop project.");
        }

        return "Project stopped!";
    }

    public ProjectGetResponse findBy(String name) {
        List<ProjectGetResponse> projects = this.getProjects();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        if (projects.isEmpty()) {
            throw ClientExceptions.noContent("Project was not found.");
        }

        return projects.getFirst();
    }

    public String scale(String p, Integer num) {
        ProjectGetResponse project = findBy(p);
        String token = tokenUtils.readToken().accessToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(num))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to change scale project.");
        }

        return "Required instances changed to " + num;
    }

    public ResponseEntity<Void> freeze(String slug) {
        ResponseEntity<Void> response = client.project().build().put()
                .uri("/{slug}/freeze", slug)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().isError()) {
            // todo: throw exception and handle it
            System.out.println("Freezing failed.");
        }

        return response;
    }


}
