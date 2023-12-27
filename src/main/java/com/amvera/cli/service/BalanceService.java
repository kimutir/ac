package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private final HttpCustomClient client;
    private final TokenUtils tokenUtils;

    public BalanceService(
            HttpCustomClient client,
            TokenUtils tokenUtils) {
        this.client = client;
        this.tokenUtils = tokenUtils;
    }

    public BalanceGetResponse getBalance() {
        String token = tokenUtils.readToken().accessToken();

        BalanceGetResponse balance = client.balance(token).build()
                .get()
                .retrieve()
                .body(BalanceGetResponse.class);

        if (balance == null) {
            throw new RuntimeException("Unable to get balance information.");
        }

        return balance;
    }
}
