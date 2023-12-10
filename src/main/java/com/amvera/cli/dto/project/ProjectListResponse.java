package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.List;

@RegisterReflectionForBinding
public class ProjectListResponse {

    @JsonProperty("services")
    private List<ProjectResponse> services;

    public List<ProjectResponse> getServices() {
        return services;
    }

    public void setServices(List<ProjectResponse> services) {
        this.services = services;
    }
}
