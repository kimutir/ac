package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final HttpCustomClient client;

    @Autowired
    public ProjectService(
            HttpCustomClient client
    ) {
        this.client = client;
    }

    public List<ProjectResponse> getProjects() {
        String token = TokenUtils.readResponseToken();

        ProjectListResponse projectList = client.project(token).build().get()
                .retrieve()
                .body(ProjectListResponse.class);

        return projectList.getServices();
    }

    public ATest createProject(String name, Integer tariff) throws JsonProcessingException {
        String token = TokenUtils.readResponseToken();

        ATest project = client.project(token).build().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .body(ATest.class);

        return project;
    }

    public void addConfig(AmveraConfiguration body, String slug) {
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/config?slug={slug}", slug, slug)
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }
    }

    public String rebuild(String p) {
        ProjectResponse project = findBy(p);
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/rebuild", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project rebuilding started...";
    }

    public String restart(String p) {
        ProjectResponse project = findBy(p);
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/restart", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project restarting started...";
    }

    public String delete(String p) {
        ProjectResponse project = findBy(p);
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().delete()
                .uri("/{slug}", project.getSlug())
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project deleted successfully!";
    }

    public String start(String p) {
        ProjectResponse project = findBy(p);
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(1))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project started!";
    }

    public String stop(String p) {
        ProjectResponse project = findBy(p);
        String token = TokenUtils.readResponseToken();

        ResponseEntity<String> response = client.project(token).build().post()
                .uri("/{slug}/scale", project.getSlug())
                .body(new ScalePostRequest(0))
                .retrieve().toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project stopped!";
    }

    public ProjectResponse findBy(String name) {
        List<ProjectResponse> projects = this.getProjects();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        //todo: throw exception
        if (projects.isEmpty()) return null;

        return projects.getFirst();
    }

    public void changeScale(Integer num) {

    }


}
