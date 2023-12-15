package com.amvera.cli.dto.project;

import java.util.List;

public record EnvListGetResponse(
        List<EnvDTO> environmentVariables
) {
}
