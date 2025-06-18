package com.amvera.cli.utils.table;

import com.amvera.cli.dto.billing.ConditionResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.billing.ConditionType;
import com.amvera.cli.dto.billing.ResourceType;
import com.amvera.cli.dto.billing.Tariff;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TariffTableModel {
    private String name;
    @JsonProperty("CPU limit")
    private BigDecimal cpu;
    @JsonProperty("RAM limit")
    private BigDecimal ram;
    @JsonProperty("SSD limit")
    private BigDecimal volume;

    public TariffTableModel(TariffResponse t) {
        this.name = Tariff.value(t.id()).name();

        List<ConditionResponse> conditions = t.conditions().stream().filter(c -> c.type().equals(ConditionType.LIMIT)).toList();

        this.cpu = conditions.stream().filter(c -> c.resourceType().equals(ResourceType.CPU)).findFirst().get().value().setScale(2, RoundingMode.CEILING);
        this.ram = conditions.stream().filter(c -> c.resourceType().equals(ResourceType.RAM)).findFirst().get().value().setScale(2, RoundingMode.CEILING);
        this.volume = conditions.stream()
                .filter(c -> c.resourceType().equals(ResourceType.VOLUME)).findFirst()
                .orElse(new ConditionResponse(ConditionType.LIMIT, ResourceType.VOLUME, BigDecimal.valueOf(25), null))
                .value().setScale(2, RoundingMode.CEILING);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCpu() {
        return cpu;
    }

    public void setCpu(BigDecimal cpu) {
        this.cpu = cpu;
    }

    public BigDecimal getRam() {
        return ram;
    }

    public void setRam(BigDecimal ram) {
        this.ram = ram;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}
