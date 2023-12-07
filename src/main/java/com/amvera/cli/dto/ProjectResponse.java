package com.amvera.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectResponse {

    /**
     * "id":10585,
     * "ownerId":"6df2158f-c101-42a5-9793-b0d9829b7564",
     * "ownerName":"kimutir",
     * "name":"ds",
     * "slug":"ds",
     * "serviceType":"compute",
     * "status":"EMPTY",
     * "statusMessage":"",
     * "requiredInstances":1,
     * "instances":0,
     * "active":true,
     * "created":1701401943.912208000,
     * "deactivated":null
     */

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

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
}
