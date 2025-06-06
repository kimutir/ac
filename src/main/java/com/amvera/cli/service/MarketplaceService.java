package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.MarketplaceConfig;
import com.amvera.cli.dto.project.config.ConfigGetResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigGetResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceService {

    private final HttpCustomClient client;

    @Autowired
    public MarketplaceService(HttpCustomClient client) {
        this.client = client;
    }


    public ResponseEntity<MarketplaceConfigGetResponse> getMarketplaceConfig() {
        return client.marketplace()
                .get()
                .uri("/configuration")
                .retrieve()
                .toEntity(MarketplaceConfigGetResponse.class);
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
