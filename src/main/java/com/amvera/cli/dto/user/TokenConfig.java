package com.amvera.cli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenConfig(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
