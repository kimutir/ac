package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import com.amvera.cli.dto.project.config.MarketplaceConfigResponse;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketClient extends BaseHttpClient {


    public MarketClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.logs(), tokenUtils.readToken().accessToken());
    }

    public ResponseEntity<MarketplaceConfigResponse> getConfig() {
        return client()
                .get()
                .uri("/configuration")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting preconfigured config template";
                    throw new ClientResponseException(msg, status);
                })
                .toEntity(MarketplaceConfigResponse.class);
    }

    public HttpStatusCode saveConfig(MarketplaceConfigPostRequest marketplaceConfig) {
        return client()
                .post()
                .uri("/config")
                .body(marketplaceConfig)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when saving preconfigured config";
                    throw new ClientResponseException(msg, status);
                })
                .toBodilessEntity()
                .getStatusCode();
    }

}
