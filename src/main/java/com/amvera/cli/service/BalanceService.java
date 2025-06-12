package com.amvera.cli.service;

import com.amvera.cli.client.BalanceClient;
import com.amvera.cli.dto.billing.BalanceResponse;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private final BalanceClient balanceClient;

    public BalanceService(BalanceClient balanceClient) {
        this.balanceClient = balanceClient;
    }

    public BalanceResponse getBalance() {
        return balanceClient.get();
    }
}
