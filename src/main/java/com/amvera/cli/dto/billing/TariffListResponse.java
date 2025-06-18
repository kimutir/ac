package com.amvera.cli.dto.billing;

import java.util.List;

public record TariffListResponse(
        List<TariffResponse> tariffs
) {
}
