package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffListResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class TariffClient extends BaseHttpClient {

    public TariffClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.tariff(), tokenUtils.readToken().accessToken());
    }

    public TariffResponse get(String slug) {
        return client()
                .get()
                .uri("/slug/{slug}?currency={currency}", slug, Currency.getInstance("RUB"))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = String.format("Error when getting tariff of %s", slug);
                    throw new ClientResponseException(msg, status);
                })
                .body(TariffResponse.class);
    }

    public TariffListResponse getAll() {
        return client()
                .get()
                .uri("?currency={currency}", Currency.getInstance("RUB"))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting tariff list";
                    throw new ClientResponseException(msg, status);
                })
                .body(TariffListResponse.class);
    }

}
