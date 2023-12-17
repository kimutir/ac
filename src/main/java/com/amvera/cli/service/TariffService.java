package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.utils.ClientExceptions;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TariffService {

    private final HttpCustomClient client;

    public TariffService(HttpCustomClient client) {
        this.client = client;
    }

    public void changeTariff(String slug, int tariffId) {
        String token = TokenUtils.readToken();
        ResponseEntity<String> response = client.tariff(token).build()
                .post().uri("/{slug}/tariff", slug)
                .body(tariffId)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Changing tariff failed.");
        }

    }

    public TariffGetResponse getTariff(String slug) {
        String token = TokenUtils.readToken();
        RestClient.Builder builder = client.tariff(token);

        TariffGetResponse tariff = builder.build().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .body(TariffGetResponse.class);

        if (tariff == null) {
            throw ClientExceptions.noContent("Tariff loading failed.");
        }

        return tariff;
    }

}
