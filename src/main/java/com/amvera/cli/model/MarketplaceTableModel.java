package com.amvera.cli.model;

import com.amvera.cli.dto.project.ProjectGetResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "slug", "status", "status message", "requires instances", "instances", "tariff"})
public class MarketplaceTableModel {
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

    public MarketplaceTableModel(ProjectGetResponse p, String tariff) {
        this.id = p.getId();
        this.name = p.getName();
        this.slug = p.getSlug();
        this.tariff = tariff;
        this.status = p.getStatus();
        this.statusMessage = p.getStatusMessage();
        this.requiredInstances = p.getRequiredInstances();
        this.instances = p.getInstances();
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
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

}
