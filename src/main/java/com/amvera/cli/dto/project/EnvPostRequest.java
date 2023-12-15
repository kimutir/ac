package com.amvera.cli.dto.project;

public record EnvPostRequest(
        String name,
        String value,
        Boolean isSecret
) {


}
