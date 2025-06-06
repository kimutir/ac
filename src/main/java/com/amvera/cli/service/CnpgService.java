package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.cnpg.CnpgPostRequest;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigGetResponse;
import com.amvera.cli.dto.project.config.MarketplaceConfigPostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CnpgService {

    private final HttpCustomClient client;

    @Autowired
    public CnpgService(HttpCustomClient client) {
        this.client = client;
    }

    public ResponseEntity<MarketplaceConfigGetResponse> creasdate() {
        return client.marketplace()
                .get()
                .uri("/configuration")
                .retrieve()
                .toEntity(MarketplaceConfigGetResponse.class);
    }

    public ResponseEntity<CnpgResponse>  create(CnpgPostRequest request) {
        return client.postgresql()
                .post()
                .body(request)
                .retrieve()
                .toEntity(CnpgResponse.class);
    }

}
