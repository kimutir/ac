package com.amvera.cli.dto.project;

public record IngressPort(
        long id,
        String urlPath,
        int port
) {
}
