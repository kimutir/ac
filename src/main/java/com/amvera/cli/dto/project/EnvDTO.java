package com.amvera.cli.dto.project;

public record EnvDTO(
        Integer id,
        String name,
        String value,
        Boolean isSecret
) {
}