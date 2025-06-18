package com.amvera.cli.dto.project.cnpg;

public record CnpgPutRequest(
        String serviceSlug,
        boolean isScheduledBackupEnabled
) {
}
