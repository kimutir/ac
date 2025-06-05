package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.config.ConfigGetResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceService {

    private final HttpCustomClient client;

    @Autowired
    public MarketplaceService(HttpCustomClient client) {
        this.client = client;
    }

    public String test() {
        return "test";
    }

    public MarketplaceConfigGetResponse getMarketplaceConfig() {
        return client.marketplace()
                .get()
                .uri("/configuration")
                .retrieve()
                .body(MarketplaceConfigGetResponse.class);
    }
}
