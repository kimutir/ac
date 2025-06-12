package com.amvera.cli.dto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainResponse{

    private Long id;
    private String domainName;
    private boolean isDefault;
    private boolean activated;
    private IngressType ingressType;
    private List<IngressPort> ingressPorts;
    @JsonSetter(nulls = Nulls.SKIP)
    private DomainType domainType = DomainType.EXTERNAL;

    public DomainResponse() {
    }

    public DomainResponse(String domainName, List<IngressPort> ingressPorts) {
        this.id = null;
        this.domainName = domainName;
        this.isDefault = true;
        this.activated = true;
        this.ingressType = IngressType.HTTP;
        this.ingressPorts = ingressPorts;
        this.domainType = DomainType.INTERNAL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public IngressType getIngressType() {
        return ingressType;
    }

    public void setIngressType(IngressType ingressType) {
        this.ingressType = ingressType;
    }

    public List<IngressPort> getIngressPorts() {
        return ingressPorts;
    }

    public void setIngressPorts(List<IngressPort> ingressPorts) {
        this.ingressPorts = ingressPorts;
    }

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
    }

    @Override
    public String toString() {
        return "DomainResponse{" +
                "id=" + id +
                ", domainName='" + domainName + '\'' +
                ", isDefault=" + isDefault +
                ", activated=" + activated +
                ", ingressType=" + ingressType +
                ", ingressPorts=" + ingressPorts +
                ", domainType=" + domainType +
                '}';
    }
}
