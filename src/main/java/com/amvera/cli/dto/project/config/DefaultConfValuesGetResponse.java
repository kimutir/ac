package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public record DefaultConfValuesGetResponse(
        @JsonProperty("default")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String defaultValue,
        String type,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Object blockedBy,
        Boolean advanced
) {
}
