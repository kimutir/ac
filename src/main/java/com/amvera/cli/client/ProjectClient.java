package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectClient extends BaseHttpClient {

    public ProjectClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.projects(), tokenUtils.readToken().accessToken());
    }

    public List<ProjectResponse> getAll() {
        return client().get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting project list";
                    throw new ClientResponseException(msg, status);
                })
                .body(ProjectListResponse.class)
                .getServices();
    }

    public void freeze(String slug) {
        client().put()
                .uri("/{slug}/freeze", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when freezing %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public void rebuild(String slug) {
        client().post()
                .uri("/{slug}/rebuild", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when rebuilding %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public void restart(String slug) {
        client().post()
                .uri("/{slug}/restart", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when restarting %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public ProjectPostResponse create(String name, Integer tariff) {
        return client().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when creating %s", name);
                    throw new ClientResponseException(msg, status);
                })
                .body(ProjectPostResponse.class);
    }

    public void addConfig(AmveraConfiguration body, String slug) {
        client().post()
                .uri(builder -> builder
                        .path("/{slug}/config")
                        .queryParam("slug", slug)
                        .build(slug)
                )
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when saving %s configuration", slug);
                    throw new ClientResponseException(msg, status);
                })
                .toEntity(String.class);
    }

    public void scale(String slug, Integer num) {
        client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(num))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when scaling %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public void stop(String slug) {
        client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(0))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when stopping %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public void start(String slug) {
        client().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(1))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when starting %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public void delete(String slug) {
        client().delete()
                .uri("/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when deleting %s", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public TariffResponse getTariff(String slug) {
        return client().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when getting %s tariff", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(TariffResponse.class);
    }

    public void updateTariff(String slug, int tariffId) {
        client()
                .post().uri("/{slug}/tariff", slug)
                .body(tariffId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when updating %s tariff", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public ProjectResponse get(String slug) {
        return client()
                .get()
                .uri("/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Could not find %s", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(ProjectResponse.class);
    }
}
