package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectRequest {
    private String name;
    private Integer tariffId;

    public ProjectRequest(String name, Integer tariffId) {
        this.name = name;
        this.tariffId = tariffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTariffId() {
        return tariffId;
    }

    public void setTariffId(Integer tariffId) {
        this.tariffId = tariffId;
    }
}
