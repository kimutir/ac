package com.amvera.cli.dto.project;

public record EnvPutRequest(
        Long id,
        String name,
        String value,
        Boolean isSecret
) {
}
