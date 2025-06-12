package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.project.config.MarketplaceConfigResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceService {

    private final AmveraHttpClient client;

    @Autowired
    public MarketplaceService(AmveraHttpClient client) {
        this.client = client;
    }


    public ResponseEntity<MarketplaceConfigResponse> getMarketplaceConfig() {
        return client.marketplace()
                .get()
                .uri("/configuration")
                .retrieve()
                .toEntity(MarketplaceConfigResponse.class);
    }

    public HttpStatusCode saveMarketplaceConfig(MarketplaceConfigPostRequest marketplaceConfig) {
        return client.marketplace()
                .post()
                .uri("/config")
                .body(marketplaceConfig)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode();
    }
}
