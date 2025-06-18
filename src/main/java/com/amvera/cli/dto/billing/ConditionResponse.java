package com.amvera.cli.dto.billing;

import java.math.BigDecimal;

public record ConditionResponse(
        ConditionType type,
        ResourceType resourceType,
        BigDecimal value,
        String description
) {
}
