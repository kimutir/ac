package com.amvera.cli.dto.project.cnpg;

public record CnpgBackupPostRequest(
        String serviceSlug,
        String description
) {
}
