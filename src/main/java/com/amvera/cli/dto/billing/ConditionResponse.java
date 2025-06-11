package com.amvera.cli.dto.billing;

import com.amvera.cli.utils.ConditionType;
import com.amvera.cli.utils.ResourceType;

import java.math.BigDecimal;

public record ConditionResponse(
        ConditionType type,
        ResourceType resourceType,
        BigDecimal value,
        String description
) {
}
