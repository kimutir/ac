package com.amvera.cli.service;

import com.amvera.cli.client.CnpgClient;
import com.amvera.cli.client.ProjectClient;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.ServiceType;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.exception.UnsupportedServiceTypeException;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.input.AmveraInput;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import com.amvera.cli.utils.table.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class ProjectService {
    private final AmveraTable table;
    private final TariffService tariffService;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;
    private final ProjectClient projectClient;
    private final CnpgClient cnpgClient;

    @Autowired
    public ProjectService(
            AmveraTable table,
            TariffService tariffService,
            ShellHelper helper,
            AmveraSelector selector,
            AmveraInput input,
            ProjectClient projectClient, CnpgClient cnpgClient
    ) {
        this.table = table;
        this.tariffService = tariffService;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
        this.projectClient = projectClient;
        this.cnpgClient = cnpgClient;
    }

    public void renderPreconfiguredTable(ProjectResponse project) {
        TariffResponse tariff = projectClient.getTariff(project.getSlug());
        helper.print(table.singleEntityTable(new MarketplaceTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderCnpgTable(String slug, CnpgResponse cnpg) {
        TariffResponse tariff = projectClient.getTariff(slug);
        helper.print(table.singleEntityTable(new CnpgTableModel(cnpg, Tariff.value(tariff.id()))));
    }

    public void renderTable(ProjectResponse project) {
        TariffResponse tariff = projectClient.getTariff(project.getSlug());
        helper.print(table.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderTariffTable(String slug) {
        ProjectResponse project = findOrSelect(slug);
        TariffResponse tariff = projectClient.getTariff(project.getSlug());
        helper.print(table.singleEntityTable(new TariffTableModel(tariff)));
    }

    public void updateTariff(String slug) {
        ProjectResponse project = findOrSelect(slug);
        Tariff tariff = tariffService.select();

        projectClient.updateTariff(project.getSlug(), tariff.id());

        helper.println("Tariff has been updated. Your project will we restarted automatically");
    }

    public void rebuild(String slug) {
        ProjectResponse project = findOrSelect(slug);

        ResponseEntity<Void> response = projectClient.rebuild(project.getSlug());

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project rebuilding started...");
        }
    }

    public void restart(String slug) {
        if (slug == null) {
            slug = select(p -> p.getServiceType().equals(ServiceType.PROJECT) || p.getServiceType().equals(ServiceType.PRECONFIGURED)).getSlug();
        }

        ResponseEntity<Void> response = projectClient.restart(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project restarting started...");
        }
    }

    public void start(String slug) {
        if (slug == null) {
            slug = select(p -> p.getInstances() == 0 && p.getRequiredInstances() > 0).getSlug();
        }

        ResponseEntity<Void> response = projectClient.start(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Starting project...");
        }
    }

    public void stop(String slug) {
        ProjectResponse project = findOrSelect(slug, p -> p.getInstances() > 0 && p.getRequiredInstances() > 0);

        ResponseEntity<Void> response = projectClient.stop(project.getSlug());

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project stopped...");
        }
    }

    public void scale(String slug, Integer instances) {
        ProjectResponse project = findOrSelect(slug, p -> !p.getStatus().equals("EMPTY"));

        if (instances == null) {
            instances = Integer.parseInt(input.notBlankOrNullInput("Enter desired replicas amount: "));
            // todo: add try catch
        }

        ResponseEntity<Void> response;

        if (project.getServiceType().equals(ServiceType.POSTGRESQL)) {
           response = cnpgClient.scale(project.getSlug(), instances);
        } else {
           response = projectClient.scale(project.getSlug(), instances);
        }


        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project scaled...");
        }
    }

    public void freeze(String slug) {
        if (slug != null) {
            ProjectResponse project = projectClient.get(slug);

            if (!project.getServiceType().equals(ServiceType.PROJECT)) {
                throw new UnsupportedServiceTypeException("Unable to freeze not PROJECT type");
            }

            slug = project.getSlug();
        } else {
            slug = select(p -> p.getServiceType().equals(ServiceType.PROJECT)).getSlug();
        }

        ResponseEntity<Void> response = projectClient.freeze(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.print(String.format("Project %s has been frozen", slug));
        }
    }


    public ResponseEntity<Void> delete(ProjectResponse project) {
        ResponseEntity<Void> response = switch (project.getServiceType()) {
            case ServiceType.PROJECT, ServiceType.PRECONFIGURED -> projectClient.delete(project.getSlug());
            case ServiceType.POSTGRESQL -> cnpgClient.delete(project.getSlug());
        };

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Deletion failed.");
        }

        return response;
    }

    public ProjectResponse findBy(String name) {
        List<ProjectResponse> projects = projectClient.getAll();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        if (projects.isEmpty()) {
            throw ClientExceptions.noContent("Project was not found.");
        }

        return projects.getFirst();
    }

    public ProjectResponse findOrSelect(String slug) {
        return slug == null ? select() : projectClient.get(slug);
    }

    public ProjectResponse findOrSelect(String slug, Predicate<ProjectResponse> predicate) {
        return slug == null ? select(predicate) : projectClient.get(slug);
    }

    public ProjectResponse select() {
        List<SelectorItem<ProjectSelectItem>> projectList = projectClient.getAll()
                .stream()
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select project: ", true).getProject();
    }

    public ProjectResponse select(Predicate<ProjectResponse> predicate) {
        List<SelectorItem<ProjectSelectItem>> projectList = projectClient.getAll()
                .stream()
                .filter(predicate)
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select project: ", true).getProject();
    }

}
