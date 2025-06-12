package com.amvera.cli.dto.project;

import com.amvera.cli.utils.select.ProjectSelectItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.shell.component.support.*;

import java.util.Objects;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse {
    private Integer id;
    private String ownerName;
    private String name;
    private String status;
    private String slug;
    private String statusMessage;
    private Integer requiredInstances;
    private Integer instances;
    private ServiceType serviceType;

    public ServiceType getServiceType() {return serviceType;}

    public void setServiceType(String serviceType) {
        this.serviceType = ServiceType.valueOfString(serviceType);
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRequiredInstances() {
        return requiredInstances;
    }

    public void setRequiredInstances(Integer requiredInstances) {
        this.requiredInstances = requiredInstances;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectResponse that = (ProjectResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(ownerName, that.ownerName) && Objects.equals(name, that.name) && Objects.equals(status, that.status) && Objects.equals(slug, that.slug) && Objects.equals(statusMessage, that.statusMessage) && Objects.equals(requiredInstances, that.requiredInstances) && Objects.equals(instances, that.instances) && Objects.equals(serviceType, that.serviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerName, name, status, slug, statusMessage, requiredInstances, instances, serviceType);
    }

    @Override
    public String toString() {
        return "ProjectGetResponse{" +
                "id=" + id +
                ", ownerName='" + ownerName + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", slug='" + slug + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                ", requiredInstances=" + requiredInstances +
                ", instances=" + instances +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }

    public SelectorItem<ProjectSelectItem> toSelectorItem() {
        ProjectSelectItem selectItem = new ProjectSelectItem(this);
        return SelectorItem.of(selectItem.getName(), selectItem);
    }
}
