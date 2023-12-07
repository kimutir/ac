package com.amvera.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
