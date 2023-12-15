package com.amvera.cli.model;

import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.utils.Tariff;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TariffTableModel {
    private String name;
    @JsonProperty("CPU limit")
    private BigDecimal cpu;
    @JsonProperty("RAM limit")
    private BigDecimal ram;
    @JsonProperty("SSD limit")
    private BigDecimal volume;

    public TariffTableModel(TariffGetResponse t) {
        this.name = Tariff.value(t.id());
        this.cpu = t.limitVcpu();
        this.ram = t.limitRamGb();
        this.volume = t.limitVolumeGb();
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
