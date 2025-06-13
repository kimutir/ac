package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ScalePostRequest;
import com.amvera.cli.dto.project.cnpg.*;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CnpgClient extends BaseHttpClient {

    public CnpgClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.postgresql(), tokenUtils.readToken().accessToken());
    }

    public CnpgResponse getDetails(String slug) {
        return client().get()
                .uri("/{slug}/details", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Unable to find '%s' postgres detailed info", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(CnpgResponse.class);
    }

    public ProjectResponse get(String slug) {
        return client().get()
                .uri("/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Unable to find '%s' postgres", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(ProjectResponse.class);
    }

    public CnpgRestoreResponse restore(String newSlug, String oldSlug, String backupName) {
        return client()
                .post().uri("/restore")
                .body(new CnpgRestorePostRequest(newSlug, oldSlug, backupName))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Unable to restore postgres from %s backup", backupName);
                    throw new ClientResponseException(msg, status);
                })
                .body(CnpgRestoreResponse.class);
    }

    public void deleteBackup(String slug, String backupName) {
        client().delete()
                .uri("/backup/{serviceSlug}/{backupName}", slug, backupName)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("%s backup deletion failed", backupName);
                    throw new ClientResponseException(msg, status);
                });
    }

    public CnpgBackupResponse createBackup(String slug, String description) {
        return client()
                .post().uri("/backup")
                .body(new CnpgBackupPostRequest(slug, description))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Backup process stared";
                    throw new ClientResponseException(msg, status);
                })
                .body(CnpgBackupResponse.class);
    }

    public List<ProjectResponse> getAll() {
        return client()
                .get().retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when retrieving postgres list";
                    throw new ClientResponseException(msg, status);
                })
                .body(ProjectListResponse.class)
                .getServices();
    }

    public void delete(String slug) {
        client()
                .delete()
                .uri("/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when deleting postgres";
                    throw new ClientResponseException(msg, status);
                })
                .toBodilessEntity();
    }

    public CnpgResponse create(CnpgPostRequest request) {
        return client()
                .post()
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error during creation postgresql";
                    throw new ClientResponseException(msg, status);
                })
                .body(CnpgResponse.class);
    }

    public void scale(String slug, int instances) {
        client()
                .post().uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(instances))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when scaling '%s' postgres", slug);
                    throw new ClientResponseException(msg, status);
                });
    }

    public List<CnpgBackupResponse> getBackupList(String slug) {
        ResponseEntity<List<CnpgBackupResponse>> response = client()
                .get().uri("/backup/{slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when retrieving postgres backup list";
                    throw new ClientResponseException(msg, status);
                })
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public CnpgResponse update(String slug, boolean isEnabled) {
        return client().put()
                .body(new CnpgPutRequest(slug, isEnabled))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when updating postgres";
                    throw new ClientResponseException(msg, status);
                })
                .body(CnpgResponse.class);
    }

    public TariffResponse getTariff(String slug) {
        return client().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting postgres tariff";
                    throw new ClientResponseException(msg, status);
                })
                .body(TariffResponse.class);
    }
}
