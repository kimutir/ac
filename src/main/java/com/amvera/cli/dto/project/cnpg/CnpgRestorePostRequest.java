package com.amvera.cli.dto.project.cnpg;

public record CnpgRestorePostRequest(
        String newServiceName,
        String oldServiceSlug,
        String backupName
) {
}
