package com.amvera.cli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record UserConfig(
        @JsonProperty("auto_update_enabled")
        boolean autoUpdateEnabled,
        @JsonProperty("last_update_checked_at")
        OffsetDateTime lastUpdateCheckedAt
) {
}
