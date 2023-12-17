package com.amvera.cli.dto.project;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.List;

@RegisterReflectionForBinding
public class ProjectListResponse {
    private List<ProjectGetResponse> services;

    public List<ProjectGetResponse> getServices() {
        return services;
    }

    public void setServices(List<ProjectGetResponse> services) {
        this.services = services;
    }
}
