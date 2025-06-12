package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectClient extends HttpClientAbs {

    public ProjectClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.projects(), tokenUtils.readToken().accessToken());
    }

    public List<ProjectResponse> getAll() {
        ProjectListResponse projectList = client().get()
                .retrieve()
                .body(ProjectListResponse.class);

        if (projectList == null || projectList.getServices().isEmpty()) {
            throw ClientExceptions.noContent("Projects were not found.");
        }

        return projectList.getServices();
    }

    public ResponseEntity<Void> freeze(String slug) {
        ResponseEntity<Void> response = client().put()
                .uri("/{slug}/freeze", slug)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().isError()) {
            // todo: throw exception and handle it
            System.out.println("Freezing failed.");
        }

        return response;
    }

    public ResponseEntity<Void> rebuild(String slug) {
        ResponseEntity<Void> response = client().post()
                .uri("/{slug}/rebuild", slug)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Rebuilding failed.");
        }

        return response;
    }

    public ResponseEntity<Void> restart(String slug) {
        ResponseEntity<Void> response = client().post()
                .uri("/{slug}/restart", slug)
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Restarting failed.");
        }

        return response;
    }

    public ProjectPostResponse create(String name, Integer tariff) {
        ProjectPostResponse project = client().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .body(ProjectPostResponse.class);

        if (project == null) {
            throw new RuntimeException("Project creation failed.");
        }

        return project;
    }

    public void addConfig(AmveraConfiguration body, String slug) {
        ResponseEntity<String> response = client().post()
                .uri(builder -> builder
                        .path("/{slug}/config")
                        .queryParam("slug", slug)
                        .build(slug)
                )
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Creating configuration failed.");
        }

        System.out.println("Config amvera.yml added");
    }

    public ResponseEntity<Void> scale(String slug, Integer num) {
        ResponseEntity<Void> response = client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(num))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to change scale project.");
        }

        return response;
    }

    public ResponseEntity<Void> stop(String slug) {
        ResponseEntity<Void> response = client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(0))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to stop project.");
        }

        return response;
    }

    public ResponseEntity<Void> start(String slug) {
        ResponseEntity<Void> response = client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(1))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to start project.");
        }

        return response;
    }

    public ResponseEntity<Void> delete(String slug) {
        return client().delete()
                .uri("/{slug}", slug)
                .retrieve().toBodilessEntity();
    }

    public TariffResponse getTariff(String slug) {
        ResponseEntity<TariffResponse> response = client().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .toEntity(TariffResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }

    public void updateTariff(String slug, int tariffId) {
        ResponseEntity<String> response = client()
                .post().uri("/{slug}/tariff", slug)
                .body(tariffId)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Changing tariff failed.");
        }
    }

    public ProjectResponse get(String slug) {
        ResponseEntity<ProjectResponse> response = client()
                .get()
                .uri("/{slug}", slug)
                .retrieve()
                .toEntity(ProjectResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // todo throw not found
        }

        return response.getBody();
    }
}
