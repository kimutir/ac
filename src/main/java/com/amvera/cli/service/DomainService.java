package com.amvera.cli.service;

import com.amvera.cli.client.DomainClient;
import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final ProjectService projectService;
    private final DomainClient domainClient;

    public DomainService(
            AmveraTable table,
            ShellHelper helper,
            ProjectService projectService,
            DomainClient domainClient
    ) {
        this.table = table;
        this.helper = helper;
        this.projectService = projectService;
        this.domainClient = domainClient;
    }

    public void renderTable(ProjectResponse project) {
        List<DomainResponse> domainList = domainClient.get(project.getSlug());

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
        List<DomainResponse> domainList = domainClient.get(slug);

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

}
