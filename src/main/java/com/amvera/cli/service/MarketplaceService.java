package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import com.amvera.cli.dto.project.config.MarketplaceConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class MarketplaceService {

    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    @Autowired
    public MarketplaceService(
            Endpoints endpoints,
            AmveraHttpClient client
    ) {
        this.endpoints = endpoints;
        this.client = client;
    }

    public MarketplaceConfigResponse getMarketplaceConfig() {
        return client.get(
                URI.create(endpoints.marketplace() + "/configuration"),
                MarketplaceConfigResponse.class,
                "Error when getting preconfigured config template"
        );
    }

    public void saveMarketplaceConfig(MarketplaceConfigPostRequest marketplaceConfig) {
        client.post(
                URI.create(endpoints.marketplace() + "/config"),
                marketplaceConfig,
                "Error when saving preconfigured config"
        );
    }
}
