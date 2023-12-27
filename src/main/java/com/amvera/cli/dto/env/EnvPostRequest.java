package com.amvera.cli.dto.env;

public record EnvPostRequest(
        String name,
        String value,
        Boolean isSecret
) {


}
