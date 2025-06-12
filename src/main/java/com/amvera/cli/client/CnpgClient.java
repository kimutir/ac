package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ScalePostRequest;
import com.amvera.cli.dto.project.cnpg.*;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CnpgClient extends BaseHttpClient {

    public CnpgClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.postgresql(), tokenUtils.readToken().accessToken());
    }

    public CnpgResponse getDetails(String slug) {
        ResponseEntity<CnpgResponse> response = client().get()
                .uri("/{slug}/details", slug)
                .retrieve()
                .toEntity(CnpgResponse.class);

        return response.getBody();
    }

    public ProjectResponse get(String slug) {
        ResponseEntity<ProjectResponse> response = client().get()
                .uri("/{slug}", slug)
                .retrieve()
                .toEntity(ProjectResponse.class);

        return response.getBody();
    }

    public CnpgRestoreResponse restore(String newSlug, String oldSlug, String backupName) {
        ResponseEntity<CnpgRestoreResponse> response = client()
                .post().uri("/restore")
                .body(new CnpgRestorePostRequest(newSlug, oldSlug, backupName))
                .retrieve()
                .toEntity(CnpgRestoreResponse.class);

        return response.getBody();
    }

    public ResponseEntity<Void> deleteBackup(String slug, String backupName) {
        return client()
                .delete().uri("/backup/{serviceSlug}/{backupName}", slug, backupName)
                .retrieve()
                .toBodilessEntity();
    }

    public CnpgBackupResponse createBackup(String slug, String description) {
        ResponseEntity<CnpgBackupResponse> response = client()
                .post().uri("/backup")
                .body(new CnpgBackupPostRequest(slug, description))
                .retrieve()
                .toEntity(CnpgBackupResponse.class);

        return response.getBody();
    }

    public List<ProjectResponse> getAll() {
        ResponseEntity<ProjectListResponse> response = client()
                .get().retrieve()
                .toEntity(ProjectListResponse.class);

        if (response.getStatusCode().isError()) {
            // todo: throw exception
        }

        return response.getBody().getServices();
    }

    public ResponseEntity<Void> delete(String slug) {
        return client()
                .delete()
                .uri("/{slug}", slug)
                .retrieve()
                .toBodilessEntity();
    }

    public ResponseEntity<CnpgResponse> create(CnpgPostRequest request) {
        return client()
                .post()
                .body(request)
                .retrieve()
                .toEntity(CnpgResponse.class);
    }

    public ResponseEntity<Void> scale(String slug, int instances) {
        return client()
                .post().uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(instances))
                .retrieve()
                .toBodilessEntity();
    }

    public List<CnpgBackupResponse> getBackupList(String slug) {
        ResponseEntity<List<CnpgBackupResponse>> response = client()
                .get().uri("/backup/{slug}", slug)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public CnpgResponse update(String slug, boolean isEnabled) {
        ResponseEntity<CnpgResponse> response = client().put()
                .body(new CnpgPutRequest(slug, isEnabled))
                .retrieve()
                .toEntity(CnpgResponse.class);

        return response.getBody();
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
}
