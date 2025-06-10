package com.amvera.cli.utils;

import com.amvera.cli.dto.project.ProjectGetResponse;

public class ProjectSelectItem {

    private final String name;
    private final ServiceType serviceType;
    private final ProjectGetResponse project;

    public ProjectSelectItem(ProjectGetResponse project) {
        this.name = project.getName();
        this.serviceType = project.getServiceType();
        this.project = project;
    }

    public String getName() {
        return String.format("%s [ %s ]", name, serviceType.name().toLowerCase());
    }

    public ProjectGetResponse getProject() {
        return project;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ]", name, serviceType.name().toLowerCase());
    }
}
