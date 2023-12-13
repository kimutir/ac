package com.amvera.cli.dto.billing;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TariffGetResponse(
        Integer id,
        String slug,
        String name,
        String serviceTypeName,
        BigDecimal price,
        String description,
        BigDecimal limitVcpu,
        BigDecimal limitRamGb,
        BigDecimal limitVolumeGb,
        Boolean active
) {
}