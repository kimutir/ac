package com.amvera.cli.dto.project.config;

import java.util.Map;

public record ConfigResponse(
        Map<String,Map<String,Map<String,Map<String, DefaultConfValuesResponse>>>> availableParameters
) {
}
