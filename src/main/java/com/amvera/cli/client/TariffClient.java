package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffListResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class TariffClient extends BaseHttpClient {

    public TariffClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.tariff(), tokenUtils.readToken().accessToken());
    }

    public TariffResponse get(String slug) {
        ResponseEntity<TariffResponse> response = client()
                .get()
                .uri("/slug/{slug}?currency={currency}", slug, Currency.getInstance("RUB"))
                .retrieve()
                .toEntity(TariffResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }

    public TariffListResponse getAll() {
        ResponseEntity<TariffListResponse> response = client()
                .get()
                .uri("?currency={currency}", Currency.getInstance("RUB"))
                .retrieve()
                .toEntity(TariffListResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }


}
