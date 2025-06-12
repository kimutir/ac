package com.amvera.cli.service;

import com.amvera.cli.client.MarketClient;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import com.amvera.cli.dto.project.config.MarketplaceConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceService {

    private final MarketClient marketClient;

    @Autowired
    public MarketplaceService(MarketClient marketClient) {
        this.marketClient = marketClient;
    }

    public ResponseEntity<MarketplaceConfigResponse> getMarketplaceConfig() {
        return marketClient.getConfig();
    }

    public HttpStatusCode saveMarketplaceConfig(MarketplaceConfigPostRequest marketplaceConfig) {
        return marketClient.saveConfig(marketplaceConfig);
    }
}
