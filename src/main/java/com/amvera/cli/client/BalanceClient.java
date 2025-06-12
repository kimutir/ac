package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.BalanceResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.stereotype.Component;

@Component
public class BalanceClient extends HttpClientAbs{
    public BalanceClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.balance(), tokenUtils.readToken().accessToken());
    }

    public BalanceResponse get() {
        BalanceResponse balance = client().get()
                .retrieve()
                .body(BalanceResponse.class);

        if (balance == null) {
            throw new RuntimeException("Unable to get balance information.");
        }

        return balance;
    }
}
