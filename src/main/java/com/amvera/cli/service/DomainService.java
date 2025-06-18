package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.DomainSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class DomainService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final ProjectService projectService;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;
    private final AmveraSelector selector;

    public DomainService(
            AmveraTable table,
            ShellHelper helper,
            ProjectService projectService,
            Endpoints endpoints,
            AmveraHttpClient client,
            AmveraSelector selector
    ) {
        this.table = table;
        this.helper = helper;
        this.projectService = projectService;
        this.endpoints = endpoints;
        this.client = client;
        this.selector = selector;
    }

    public void renderTable(ProjectResponse project) {
        List<DomainResponse> domainList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.domain() + "/{slug}").build(project.getSlug()),
                new ParameterizedTypeReference<>() {
                },
                "Error when getting project domain list"
        );

        switch (project.getServiceType()) {
            case PROJECT ->
                    domainList.add(new DomainResponse(String.format("amvera-%s-run-%s", project.getOwnerName(), project.getSlug()), null));
            case PRECONFIGURED -> {
            }
            case POSTGRESQL -> {
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-rw", project.getOwnerName(), project.getSlug()), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-ro", project.getOwnerName(), project.getSlug()), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-r", project.getOwnerName(), project.getSlug()), null));
            }
        }

        if (domainList.isEmpty()) {
            helper.printWarning("No domains found.");
//            helper.printWarning("No domains found. You can start with 'amvera create domain'");
        } else {
            helper.println(table.domains(domainList));
        }
    }

    public void renderTable(String slug) {
        ProjectResponse project = projectService.findOrSelect(slug);
        renderTable(project);
    }

    public void renderTable(String slug, String ownerName, ServiceType serviceType) {
        List<DomainResponse> domainList = client.get(
                UriComponentsBuilder.fromUriString(endpoints.domain() + "/{slug}").build(slug),
                new ParameterizedTypeReference<List<DomainResponse>>() {
                },
                "Error when getting project domain list"
        );

        switch (serviceType) {
            case PROJECT ->
                    domainList.add(new DomainResponse(String.format("amvera-%s-run-%s", ownerName, slug), null));
            case PRECONFIGURED -> {
            }
            case POSTGRESQL -> {
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-rw", ownerName, slug), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-ro", ownerName, slug), null));
                domainList.add(new DomainResponse(String.format("amvera-%s-cnpg-%s-r", ownerName, slug), null));
            }
        }

        if (domainList.isEmpty())
            throw new EmptyValueException("No domains found. You can start with 'amvera domain add''");

        helper.println(table.domains(domainList));
    }

    public void delete(String slug, Long id) {
        ProjectResponse project = projectService.findOrSelect(slug);
        DomainResponse domain = select(project);

        client.delete(
                UriComponentsBuilder.fromUriString(endpoints.domain() + "/{slug}/{id}").build(project.getSlug(), domain.getId()),
                "Error when deleting domain " + id
        );

        helper.println("Domain has been deleted");
    }

    public DomainResponse getOrSelect(String slug, Long id) {
        ProjectResponse project = projectService.findOrSelect(slug);

        return id == null ? select(project) : get(project, id);
    }

    public DomainResponse select(ProjectResponse project) {
        List<SelectorItem<DomainSelectItem>> domainList = client
                .get(
                        UriComponentsBuilder.fromUriString(endpoints.domain() + "/{slug}").build(project.getSlug()),
                        new ParameterizedTypeReference<List<DomainResponse>>() {
                        },
                        "Error when getting domain list"
                )
                .stream()
                .map(DomainResponse::toSelectorItem).toList();

        if (domainList.isEmpty()) {
            throw new EmptyValueException("No user domains found. You can start with 'amvera domain add'");
        }

        return selector.singleSelector(domainList, "Select domain: ", true).getDomain();
    }

    public DomainResponse get(ProjectResponse project, Long id) {
        return client.get(
                UriComponentsBuilder.fromUriString(endpoints.domain() + "/{slug}/{id}").build(project.getSlug(), id),
                DomainResponse.class,
                "Error when getting project domain"
        );
    }

}
