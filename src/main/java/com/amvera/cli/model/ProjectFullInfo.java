package com.amvera.cli.model;

import com.amvera.cli.dto.project.ProjectResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "slug", "status", "status message", "requires instances", "instances", "git clone", "git remote", "domain", "url"})
public class ProjectFullInfo {
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
    private String domain;
    private String url;

    public ProjectFullInfo(ProjectResponse p, String tariff) {
        this.ownerName = p.getOwnerName();
        this.id = p.getId();
        this.name = p.getName();
        this.slug = p.getSlug();
        this.tariff = tariff;
        this.instances = p.getInstances();
        this.requiredInstances = p.getRequiredInstances();
        this.status = p.getStatus();
        this.statusMessage = p.getStatusMessage();
        this.git = this.git + ownerName + "/" + slug;
        this.clone = "git clone " + git;
        this.remote = "git remote add amvera " + git;
        this.domain = "amvera-" + ownerName + "-run-" + slug;
        this.url = String.format("https://%s-%s.amvera.io", slug, ownerName);
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
}
