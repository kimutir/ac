package com.amvera.cli.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Integer id;
    private String name;
    private String status;
    private Integer requiredInstances;
    private Integer instances;

    public String[] toArray() {
        String[] arr = new String[5];
        arr[0] = id.toString();
        arr[1] = name;
        arr[2] = status;
        arr[3] = requiredInstances.toString();
        arr[4] = instances.toString();

        return arr;
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
}
