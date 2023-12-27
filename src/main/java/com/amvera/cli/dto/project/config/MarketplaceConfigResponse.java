package com.amvera.cli.dto.project.config;

import java.util.Map;

public record MarketplaceConfigResponse(
        Map<String,Map<String,Map<String, Map<String,Map<String, DefaultConfValuesResponse>>>>> availableParameters
) {
}
