package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectPostResponse(
        String username,
        String name,
        String slug,
        Boolean ready,
        Integer instances,
        Integer requiredInstances,
        String buildStatus,
        String buildStatusMessage
) {
}