package com.amvera.cli.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoResponse(
        String username,
        String firstName,
        String lastName,
        String email
) {
}
