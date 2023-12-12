package com.amvera.cli.dto.project;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.List;

@RegisterReflectionForBinding
public class ProjectListResponse {
    private List<ProjectResponse> services;

    public List<ProjectResponse> getServices() {
        return services;
    }

    public void setServices(List<ProjectResponse> services) {
        this.services = services;
    }
}
