package com.amvera.cli.utils.select;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.utils.ServiceType;

public class ProjectSelectItem {

    private final String name;
    private final ServiceType serviceType;
    private final ProjectResponse project;

    public ProjectSelectItem(ProjectResponse project) {
        this.name = project.getName();
        this.serviceType = project.getServiceType();
        this.project = project;
    }

    public String getName() {
        return String.format("%s [ %s ]", name, serviceType.name().toLowerCase());
    }

    public ProjectResponse getProject() {
        return project;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ]", name, serviceType.name().toLowerCase());
    }
}
