package com.amvera.cli.utils.table;

import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ProjectPostResponse;
import com.amvera.cli.utils.Tariff;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "slug", "status", "status message", "requires instances", "instances", "git clone", "git remote", "domain", "url"})
public class ProjectTableModel {
    private String git = "https://git.amvera.ru/";
    private String ownerName;
    private Integer id;
    private String name;
    private String slug;
    private String tariff;
    private String status;
    @JsonProperty("status message")
    private String statusMessage;
    @JsonProperty("requires instances")
    private Integer requiredInstances;
    private Integer instances;
    @JsonProperty("git clone")
    private String clone;
    @JsonProperty("git remote")
    private String remote;

    public ProjectTableModel(ProjectResponse p, Tariff tariff) {
        this.ownerName = p.getOwnerName();
        this.id = p.getId();
        this.name = p.getName();
        this.slug = p.getSlug();
        this.tariff = tariff.name();
        this.instances = p.getInstances();
        this.requiredInstances = p.getRequiredInstances();
        this.status = p.getStatus();
        this.statusMessage = p.getStatusMessage();
        this.git = this.git + ownerName + "/" + slug;
        this.clone = "git clone " + git;
        this.remote = "git remote add amvera " + git;
    }


    public ProjectTableModel(ProjectPostResponse p, Tariff tariff) {
        this.ownerName = p.username();
        this.name = p.name();
        this.slug = p.slug();
        this.tariff = tariff.name();
        this.instances = p.instances();
        this.requiredInstances = p.requiredInstances();
        this.status = p.buildStatus();
        this.statusMessage = p.buildStatusMessage();
        this.git = this.git + ownerName + "/" + slug;
        this.clone = "git clone " + git;
        this.remote = "git remote add amvera " + git;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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

    public String getClone() {
        return clone;
    }

    public void setClone(String clone) {
        this.clone = clone;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
}
