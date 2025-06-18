package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogResponse(
       String content,
       String stream,
       String timestamp
) {
}
