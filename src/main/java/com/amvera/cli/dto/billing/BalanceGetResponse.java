package com.amvera.cli.dto.billing;

import java.math.BigDecimal;

public record BalanceGetResponse(
        String userUid,
        BigDecimal balance,
        String currency
) {
}
