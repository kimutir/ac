package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ScalePostRequest;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.exception.EmptyValueException;
import com.amvera.cli.exception.UnsupportedServiceTypeException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.input.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import com.amvera.cli.utils.table.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

@Service
public class ProjectService {
    private final AmveraTable table;
    private final TariffService tariffService;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;
    private final AmveraHttpClient client;
    private final Endpoints endpoints;

    @Autowired
    public ProjectService(
            AmveraTable table,
            TariffService tariffService,
            ShellHelper helper,
            AmveraSelector selector,
            AmveraInput input,
            AmveraHttpClient client,
            Endpoints endpoints
    ) {
        this.table = table;
        this.tariffService = tariffService;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
        this.client = client;
        this.endpoints = endpoints;
    }

    public void renderPreconfiguredTable(ProjectResponse project) {
        TariffResponse tariff = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/tariff").build(project.getSlug()),
                TariffResponse.class,
                String.format("Error when getting %s tariff", project.getSlug())
        );
        helper.print(table.singleEntityTable(new MarketplaceTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderCnpgTable(String slug, CnpgResponse cnpg) {
        TariffResponse tariff = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/tariff").build(slug),
                TariffResponse.class,
                String.format("Error when getting %s tariff", slug)
        );
        helper.print(table.singleEntityTable(new CnpgTableModel(cnpg, Tariff.value(tariff.id()))));
    }

    public void renderTable(Predicate<ProjectResponse> predicate) {
        List<ProjectResponse> projectList = findAll(predicate);

        if (projectList.isEmpty())
            throw new EmptyValueException("No services found. You can start with 'amvera create'");

        helper.print(table.projects(projectList));
    }

    public void renderTable() {
        List<ProjectResponse> projectList = findAll();

        if (projectList.isEmpty())
            throw new EmptyValueException("No services found. You can start with 'amvera create'");

        helper.print(table.projects(projectList));
    }

    public void renderTable(ProjectResponse project) {
        TariffResponse tariff = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/tariff").build(project.getSlug()),
                TariffResponse.class,
                String.format("Error when getting %s tariff", project.getSlug())
        );
        helper.print(table.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderTariffTable(String slug) {
        ProjectResponse project = findOrSelect(slug);
        TariffResponse tariff = client.get(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/tariff").build(project.getSlug()),
                TariffResponse.class,
                String.format("Error when getting %s tariff", project.getSlug())
        );
        helper.print(table.singleEntityTable(new TariffTableModel(tariff)));
    }

    public void updateTariff(String slug) {
        ProjectResponse project = findOrSelect(slug);
        Tariff tariff = tariffService.select();

        client.post(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/tariff").build(project.getSlug()),
                String.format("Error when getting %s tariff", project.getSlug()),
                tariff.id()
        );

        helper.println("Tariff has been updated. Your project will we restarted automatically");
    }

    public void rebuild(String slug) {
        ProjectResponse project = findOrSelect(slug);
        client.post(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/rebuild").build(slug),
                String.format("Error when rebuilding %s", slug)
        );
        helper.println("Project rebuilding started...");
    }

    public void restart(String slug) {
        slug = findOrSelect(slug, p -> p.getServiceType().equals(ServiceType.PROJECT) || p.getServiceType().equals(ServiceType.PRECONFIGURED)).getSlug();
        client.post(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/restart").build(slug),
                String.format("Error when restarting %s", slug)
        );
        helper.println("Project restarting started...");
    }

    public void start(String slug) {
        slug = findOrSelect(slug, p -> p.getInstances() == 0 && p.getRequiredInstances() > 0).getSlug();
        client.post(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/rebuild").build(slug),
                String.format("Error when starting %s", slug),
                new ScalePostRequest(1)
        );
        helper.println("Starting project...");
    }

    public void stop(String slug) {
        slug = findOrSelect(slug, p -> p.getInstances() > 0 && p.getRequiredInstances() > 0).getSlug();
        client.post(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/scale").build(slug),
                String.format("Error when stopping %s", slug),
                new ScalePostRequest(0)
        );
        helper.println("Project stopped...");
    }

    public void scale(String slug, Integer instances) {
        ProjectResponse project = findOrSelect(slug, p -> !p.getStatus().equals("EMPTY"));

        while (instances == null) {
            try {
                instances = Integer.parseInt(input.notBlankOrNullInput("Enter desired replicas amount: "));
            } catch (NumberFormatException e) {
                helper.printError("Enter integer");
                instances = null;
            }
        }

        if (project.getServiceType().equals(ServiceType.POSTGRESQL)) {
            client.post(
                    UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/{slug}/scale").build(project.getSlug()),
                    String.format("Error when scaling '%s' postgres", project.getSlug()),
                    new ScalePostRequest(instances)
            );
        } else {
            client.post(
                    UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/scale").build(slug),
                    String.format("Error when scaling %s", slug),
                    new ScalePostRequest(instances)
            );
        }

        helper.println("Project scaled...");
    }

    public void freeze(String slug) {
        if (slug != null) {
            ProjectResponse project = client.get(
                    UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(slug),
                    ProjectResponse.class,
                    String.format("Could not find %s", slug)
            );

            if (!project.getServiceType().equals(ServiceType.PROJECT)) {
                throw new UnsupportedServiceTypeException("Unable to freeze not PROJECT type");
            }

            slug = project.getSlug();
        } else {
            slug = select(p -> p.getServiceType().equals(ServiceType.PROJECT)).getSlug();
        }

        client.put(
                UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}/freeze").build(slug),
                String.format("Error when freezing %s", slug)
        );

        helper.print(String.format("Project %s has been frozen", slug));
    }


    public void delete(ProjectResponse project) {
        switch (project.getServiceType()) {
            case ServiceType.PROJECT, ServiceType.PRECONFIGURED -> client.delete(
                    UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(project.getSlug()),
                    String.format("Error when deleting %s", project.getSlug())
            );
            case ServiceType.POSTGRESQL -> client.delete(
                    UriComponentsBuilder.fromUriString(endpoints.postgresql() + "/{slug}").build(project.getSlug()),
                    "Error when deleting postgres"
            );
        }
        ;
        helper.println(String.format("Project %s has been deleted", project.getSlug()));
    }

    public List<ProjectResponse> findAll() {
        return client.get(
                URI.create(endpoints.projects()),
                ProjectListResponse.class,
                "Error when getting project list"
        ).getServices();
    }

    public List<ProjectResponse> findAll(Predicate<ProjectResponse> predicate) {
        return findAll()
                .stream()
                .filter(predicate)
                .toList();
    }

    public ProjectResponse findBy(String name) {
        List<ProjectResponse> projects = findAll();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        if (projects.isEmpty()) {
            throw ClientExceptions.noContent("Project was not found.");
        }

        return projects.getFirst();
    }

    public ProjectResponse findOrSelect(String slug) {
        return findOrSelect(slug, p -> true);
    }

    public ProjectResponse findOrSelect(String slug, Predicate<ProjectResponse> predicate) {
        return slug == null ?
                select(predicate) :
                client.get(
                        UriComponentsBuilder.fromUriString(endpoints.projects() + "/{slug}").build(slug),
                        ProjectResponse.class,
                        String.format("Could not find %s", slug)
                );
    }

    public ProjectResponse select() {
        return select(p -> true);
    }

    public ProjectResponse select(Predicate<ProjectResponse> predicate) {
        List<SelectorItem<ProjectSelectItem>> projectList = findAll()
                .stream()
                .filter(predicate)
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select project: ", true).getProject();
    }

}
