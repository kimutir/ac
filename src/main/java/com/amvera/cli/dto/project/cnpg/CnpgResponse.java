package com.amvera.cli.dto.project.cnpg;

public record CnpgResponse(
        String username,
        String name,
        String postgresVersion,
        String database,
        String dbUsername,
        int instances,
        boolean enableSuperuserAccess,
        String status,
        boolean isScheduledBackupEnabled,
        int backupRetentionPolicy,
        String namespace
) {
}
