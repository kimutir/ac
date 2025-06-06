package com.amvera.cli.model;

import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "db name", "db owner name", "instances", "is super user access enabled", "is scheduled backup enabled", "tariff"})
public class CnpgTableModel {
    private String name;
    @JsonProperty("db name")
    private String database;
    @JsonProperty("db owner name")
    private String dbUsername;
    private int instances;
    @JsonProperty("is super user access enabled")
    private Boolean enableSuperuserAccess;
    private String status;
    @JsonProperty("is scheduled backup enabled")
    private boolean isScheduledBackupEnabled;
    private String tariff;

    public CnpgTableModel(CnpgResponse c, String tariff) {
        this.name = c.name();
        this.database = c.database();
        this.dbUsername = c.dbUsername();
        this.instances = c.instances();
        this.enableSuperuserAccess = c.enableSuperuserAccess();
        this.status = c.status();
        this.isScheduledBackupEnabled = c.isScheduledBackupEnabled();
        this.tariff = tariff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public Boolean getEnableSuperuserAccess() {
        return enableSuperuserAccess;
    }

    public void setEnableSuperuserAccess(Boolean enableSuperuserAccess) {
        this.enableSuperuserAccess = enableSuperuserAccess;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getIsScheduledBackupEnabled() {
        return isScheduledBackupEnabled;
    }

    public void setIsScheduledBackupEnabled(boolean scheduledBackupEnabled) {
        isScheduledBackupEnabled = scheduledBackupEnabled;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
}
