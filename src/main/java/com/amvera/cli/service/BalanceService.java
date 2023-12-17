package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private final HttpCustomClient client;

    public BalanceService(
            HttpCustomClient client
    ) {
        this.client = client;
    }

    public BalanceGetResponse getBalance() {
        String token = TokenUtils.readToken();

        BalanceGetResponse balance = client.balance(token).build()
                .get()
                .retrieve()
                .body(BalanceGetResponse.class);

        return balance;

    }
}
