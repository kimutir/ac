package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private String environment;

    private Toolchain toolchain = new Toolchain();

    public String getEnvironment() {
        return environment;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    public Toolchain getToolchain() {
        return toolchain;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setToolchain(Toolchain toolchain) {
        this.toolchain = toolchain;
    }
}
