package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.HashMap;
import java.util.Map;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private String environment;

    private Map<String, Object> toolchain = new HashMap<>();

    public String getEnvironment() {
        return environment;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Map<String, Object> getToolchain() {
        return toolchain;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setToolchain(Map<String, Object> toolchain) {
        this.toolchain = toolchain;
    }
}
