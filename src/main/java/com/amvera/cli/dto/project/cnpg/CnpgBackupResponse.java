package com.amvera.cli.dto.project.cnpg;

import org.springframework.shell.component.support.SelectorItem;

public class CnpgBackupResponse{

    private long id;
    private String  name;
    private String description;
    private boolean isScheduled;
    private String createdAt;
    private CnpgResourceStatus status;
    private String backupId;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public CnpgResourceStatus getStatus() {
        return status;
    }

    public void setStatus(CnpgResourceStatus status) {
        this.status = status;
    }

    public String getBackupId() {
        return backupId;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ]", this.name, this.status);
    }

    public SelectorItem<CnpgBackupResponse> toSelectorItem() {
        return SelectorItem.of(this.name, this);
    }
}
