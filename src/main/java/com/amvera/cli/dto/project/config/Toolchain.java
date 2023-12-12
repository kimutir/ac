package com.amvera.cli.dto.project.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class Toolchain {
    private String name;
    private String version;
    public String getName() {
        return name;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setName(String name) {
        this.name = name;
    }
    public String getVersion() {
        return version;
    }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setVersion(String version) {
        this.version = version;
    }
}
