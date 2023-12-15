package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogGetResponse(
       String content
) {
}
