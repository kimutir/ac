package com.amvera.cli.dto.project.config;

import java.util.Map;

public record MarketplaceConfigGetResponse(
        Map<String,Map<String,Map<String, Map<String,Map<String,DefaultConfValuesGetResponse>>>>> availableParameters
) {
}
