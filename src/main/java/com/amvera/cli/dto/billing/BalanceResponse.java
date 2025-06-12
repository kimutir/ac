package com.amvera.cli.dto.billing;

import java.math.BigDecimal;

public record BalanceResponse(
        String userUid,
        BigDecimal balance,
        String currency
) {
}
