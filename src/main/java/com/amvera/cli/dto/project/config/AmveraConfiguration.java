package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.HashMap;
import java.util.Map;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmveraConfiguration {
    private Meta meta = new Meta();
    private Map<String, Object> build = new HashMap<>();
    private Map<String, Object> run = new HashMap<>();

    public Meta getMeta() {
        return meta;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Map<String, Object> getBuild() {
        return build;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setBuild(Map<String, Object> build) {
        this.build = build;
    }

    public Map<String, Object> getRun() {
        return run;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setRun(Map<String, Object> run) {
        this.run = run;
    }
}

