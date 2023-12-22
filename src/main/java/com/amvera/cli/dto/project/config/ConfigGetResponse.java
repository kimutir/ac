package com.amvera.cli.dto.project.config;

import java.util.Map;

public record ConfigGetResponse (
        Map<String,Map<String,Map<String,Map<String,DefaultConfValuesGetResponse>>>> availableParameters
) {
}
