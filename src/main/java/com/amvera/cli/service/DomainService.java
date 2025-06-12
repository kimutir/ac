package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.project.DomainResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.ServiceType;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainService {

    private final AmveraHttpClient client;
    private final AmveraTable table;
    private final ShellHelper helper;
    private final ProjectService projectService;

    public DomainService(AmveraHttpClient client, AmveraTable table, ShellHelper helper, ProjectService projectService) {
        this.client = client;
        this.table = table;
        this.helper = helper;
        this.projectService = projectService;
    }

    public void renderTable(ProjectResponse project) {
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


    public void renderTable(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);
        renderTable(project);
    }

    public void renderTable(String slug, String ownerName, ServiceType serviceType) {
        List<DomainResponse> domainList = getDomains(slug);

        switch (serviceType) {
            case PROJECT -> domainList.add(new DomainResponse(String.format("amvera-%s-run-%s", ownerName, slug), null));
            case PRECONFIGURED -> {}
            case POSTGRESQL -> {
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-rw", ownerName, slug), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-ro", ownerName, slug), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-r", ownerName, slug), null));
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
