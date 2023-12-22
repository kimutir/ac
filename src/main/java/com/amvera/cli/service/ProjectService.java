package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.dto.project.config.ConfigGetResponse;
import com.amvera.cli.dto.project.config.DefaultConfValuesGetResponse;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
    private final HttpCustomClient client;

    @Autowired
    public ProjectService(
            HttpCustomClient client
    ) {
        this.client = client;
    }

    public List<ProjectGetResponse> getProjects() {
        String token = TokenUtils.readToken();

        ProjectListResponse projectList = client.project(token).build().get()
                .retrieve()
                .body(ProjectListResponse.class);

        if (projectList == null || projectList.getServices().isEmpty()) {
            throw ClientExceptions.noContent("Projects were not found.");
        }

        return projectList.getServices();
    }

    public Map<String, Map<String, Map<String, Map<String, DefaultConfValuesGetResponse>>>> getConfig() {
        String token = TokenUtils.readToken();

        ConfigGetResponse config = client.configurations(token).build().get()
                .retrieve()
                .body(ConfigGetResponse.class);

        if (config == null) {
            throw ClientExceptions.noContent("Config default values were not found.");
        }

        return config.availableParameters();
    }

    public ProjectPostResponse createProject(String name, Integer tariff) throws JsonProcessingException {
        String token = TokenUtils.readToken();

        ProjectPostResponse project = client.project(token).build().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .body(ProjectPostResponse.class);

        if (project == null) {
            throw new RuntimeException("Project creation failed.");
        }

        return project;
    }

    public void addConfig(AmveraConfiguration body, String slug) {
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/config?slug={slug}", slug, slug)
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Creating configuration failed.");
        }

        System.out.println("config added");
    }

    public String rebuild(String p) {
        ProjectGetResponse project = findBy(p);
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/rebuild", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Rebuilding failed.");
        }

        return "Project rebuilding started...";
    }

    public String restart(String p) {
        ProjectGetResponse project = findBy(p);
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/restart", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Restarting failed.");
        }

        return "Project restarting started...";
    }

    public String delete(String p) {
        ProjectGetResponse project = findBy(p);
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.project(token).build().delete()
                .uri("/{slug}", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Deletion failed.");
        }

        return "Project has been deleted successfully!";
    }

    public String start(String p) {
        ProjectGetResponse project = findBy(p);
        String token = TokenUtils.readToken();

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
        String token = TokenUtils.readToken();

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
        ;

        return projects.getFirst();
    }

    public String scale(String p, Integer num) {
        ProjectGetResponse project = findBy(p);
        String token = TokenUtils.readToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(num))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to change scale project.");
        }

        return "Required instances changed to " + num;
    }


}
