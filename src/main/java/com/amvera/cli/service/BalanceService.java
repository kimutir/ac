package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.billing.BalanceResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private final AmveraHttpClient client;
    private final TokenUtils tokenUtils;

    public BalanceService(
            AmveraHttpClient client,
            TokenUtils tokenUtils) {
        this.client = client;
        this.tokenUtils = tokenUtils;
    }

    public BalanceResponse getBalance() {
        String token = tokenUtils.readToken().accessToken();

        BalanceResponse balance = client.balance(token).build()
                .get()
                .retrieve()
                .body(BalanceResponse.class);

        if (balance == null) {
            throw new RuntimeException("Unable to get balance information.");
        }

        return balance;
    }
}
