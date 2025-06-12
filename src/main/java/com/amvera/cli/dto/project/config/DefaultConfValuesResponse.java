package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DefaultConfValuesResponse(
        @JsonProperty("default")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String defaultValue,
        String type,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Object blockedBy,
        Boolean advanced
) {
}
