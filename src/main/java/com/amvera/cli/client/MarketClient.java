package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import com.amvera.cli.dto.project.config.MarketplaceConfigResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketClient extends HttpClientAbs {


    public MarketClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.logs(), tokenUtils.readToken().accessToken());
    }

    public ResponseEntity<MarketplaceConfigResponse> getConfig() {
        return client()
                .get()
                .uri("/configuration")
                .retrieve()
                .toEntity(MarketplaceConfigResponse.class);
    }

    public HttpStatusCode saveConfig(MarketplaceConfigPostRequest marketplaceConfig) {
        return client()
                .post()
                .uri("/config")
                .body(marketplaceConfig)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode();
    }

}
