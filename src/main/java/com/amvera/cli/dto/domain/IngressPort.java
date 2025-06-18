package com.amvera.cli.dto.domain;

public record IngressPort(
        long id,
        String urlPath,
        int port
) {
}
