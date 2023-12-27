package com.amvera.cli.utils.select;

import com.amvera.cli.dto.domain.DomainResponse;

public class DomainSelectItem {

    private final String name;
    private final DomainResponse domain;

    public DomainSelectItem(DomainResponse d) {
        this.name = d.getDomainName();
        this.domain = d;
    }

    public String getName() {
        return String.format("%s [ %s ]", this.name, this.domain.getIngressType());
    }

    public DomainResponse getDomain() {
        return this.domain;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ]", this.name, this.domain.getIngressType());
    }

}
