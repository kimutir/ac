package com.amvera.cli.dto.project;

public record EnvPutRequest(
        Integer id,
        String name,
        String value,
        Boolean isSecret
) {
}
