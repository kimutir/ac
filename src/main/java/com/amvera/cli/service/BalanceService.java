package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.BalanceResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class BalanceService {
    private final AmveraHttpClient client;
    private final Endpoints endpoints;

    public BalanceService(
            AmveraHttpClient client, Endpoints endpoints
    ) {
        this.client = client;
        this.endpoints = endpoints;
    }

    public BalanceResponse getBalance() {
        return client.get(
                URI.create(endpoints.balance()),
                BalanceResponse.class,
                "Error when getting balance"
        );
    }
}
