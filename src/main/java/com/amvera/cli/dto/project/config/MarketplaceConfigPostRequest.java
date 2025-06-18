package com.amvera.cli.dto.project.config;

import com.amvera.cli.dto.env.EnvPostRequest;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;
import java.util.Map;

public class MarketplaceConfigPostRequest {
    @JsonSetter(nulls = Nulls.SKIP)
    private String version;
    private String name;
    private int tariffId;
    @JsonSetter(nulls = Nulls.SKIP)
    private Meta meta;
    @JsonSetter(nulls = Nulls.SKIP)
    private Map<String, Object> run;
    @JsonSetter(nulls = Nulls.SKIP)
    private List<EnvPostRequest> envvars;

    @Override
    public String toString() {
        return "MarketplaceConfigPostRequest{" +
                "version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", tariffId=" + tariffId +
                ", meta=" + meta +
                ", run=" + run +
                ", envvars=" + envvars +
                '}';
    }

    public MarketplaceConfigPostRequest() {
    }

    public MarketplaceConfigPostRequest(String version, String name, int tariffId, Meta meta, Map<String, Object> run, List<EnvPostRequest> envvars) {
        this.version = version;
        this.name = name;
        this.tariffId = tariffId;
        this.meta = meta;
        this.run = run;
        this.envvars = envvars;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Map<String, Object> getRun() {
        return run;
    }

    public void setRun(Map<String, Object> run) {
        this.run = run;
    }

    public List<EnvPostRequest> getEnvvars() {
        return envvars;
    }

    public void setEnvvars(List<EnvPostRequest> envvars) {
        this.envvars = envvars;
    }
}
