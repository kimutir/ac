package com.amvera.cli.service;

import com.amvera.cli.client.HttpCustomClient;
import com.amvera.cli.dto.project.DomainResponse;
import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.utils.AmveraTable;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainService {

    private final HttpCustomClient client;
    private final AmveraTable table;
    private final ShellHelper helper;

    public DomainService(HttpCustomClient client, AmveraTable table, ShellHelper helper) {
        this.client = client;
        this.table = table;
        this.helper = helper;
    }

    public void renderTable(ProjectGetResponse project) {
        List<DomainResponse> domainList = getDomains(project.getSlug());

        switch (project.getServiceType()) {
            case PROJECT -> domainList.add(new DomainResponse(String.format("amvera-%s-run-%s", project.getOwnerName(), project.getSlug()), null));
            case PRECONFIGURED -> {}
            case POSTGRESQL -> {
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-rw", project.getOwnerName(), project.getSlug()), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-ro", project.getOwnerName(), project.getSlug()), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-r", project.getOwnerName(), project.getSlug()), null));
            }
        }

        if (domainList.isEmpty()) {
            helper.printWarning("No domains found. You can start with 'amvera create domain'\n");
        } else {
            helper.println(table.domains(domainList));
        }
    }

    public List<DomainResponse> getDomains(String slug) {
        ResponseEntity<List<DomainResponse>> domainResponse = client.domain()
                .get().uri("/{slug}", slug).retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return domainResponse.getBody();
    }

}
