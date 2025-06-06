package com.amvera.cli.dto.project.cnpg;

public record CnpgPostRequest(
        String name,
        int tariffId,
        String postgresVersion,
        String database,
        String dbUsername,
        String dbPassword,
        int instances,
        boolean enableSuperuserAccess,
        String superuserPassword
) {
}
