package com.amvera.cli.dto.env;

public record EnvPutRequest(
        Long id,
        String name,
        String value,
        Boolean isSecret
) {
}
