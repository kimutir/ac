package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.BalanceResponse;
import com.amvera.cli.exception.ResourceNotFoundException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class BalanceClient extends BaseHttpClient {
    public BalanceClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.balance(), tokenUtils.readToken().accessToken());
    }

    public BalanceResponse get() {
        return client().get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    throw new ResourceNotFoundException(status + ". Unable to get balance");
                })
                .body(BalanceResponse.class);
    }
}
