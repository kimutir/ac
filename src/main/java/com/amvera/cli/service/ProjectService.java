package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.cnpg.CnpgResponse;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.dto.project.config.ConfigGetResponse;
import com.amvera.cli.exception.ClientExceptions;
import com.amvera.cli.exception.UnsupportedServiceTypeException;
import com.amvera.cli.utils.table.*;
import com.amvera.cli.utils.*;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.ProjectSelectItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class ProjectService {
    private final AmveraHttpClient client;
    private final TokenUtils tokenUtils;
    private final CnpgService cnpgService;
    private final AmveraTable table;
    private final TariffService tariffService;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final AmveraInput input;

    @Autowired
    public ProjectService(
            AmveraHttpClient client,
            TokenUtils tokenUtils,
            CnpgService cnpgService,
            AmveraTable table,
            TariffService tariffService,
            ShellHelper helper,
            AmveraSelector selector, AmveraInput input
    ) {
        this.client = client;
        this.tokenUtils = tokenUtils;
        this.cnpgService = cnpgService;
        this.table = table;
        this.tariffService = tariffService;
        this.helper = helper;
        this.selector = selector;
        this.input = input;
    }

    public void renderPreconfiguredTable(ProjectResponse project) {
        TariffResponse tariff = getTariffRequest(project.getSlug());
        helper.print(table.singleEntityTable(new MarketplaceTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderCnpgTable(String slug, CnpgResponse cnpg) {
        TariffResponse tariff = getTariffRequest(slug);
        helper.print(table.singleEntityTable(new CnpgTableModel(cnpg, Tariff.value(tariff.id()))));
    }

    public void renderTable(ProjectResponse project) {
        TariffResponse tariff = getTariffRequest(project.getSlug());
        helper.print(table.singleEntityTable(new ProjectTableModel(project, Tariff.value(tariff.id()))));
    }

    public void renderTariffTable(String slug) {
        ProjectResponse project = findOrSelect(slug);
        TariffResponse tariff = getTariffRequest(project.getSlug());
        helper.print(table.singleEntityTable(new TariffTableModel(tariff)));
    }

    public void updateTariff(String slug) {
        ProjectResponse project = findOrSelect(slug);
        Tariff tariff = tariffService.select();

        updateTariffRequest(project.getSlug(), tariff.id());

        helper.println("Tariff has been updated. Your project will we restarted automatically");
    }

    public void rebuild(String slug) {
        ProjectResponse project = findOrSelect(slug);

        ResponseEntity<Void> response = rebuildRequest(project.getSlug());

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project rebuilding started...");
        }
    }

    public void restart(String slug) {
        if (slug == null) {
            slug = select(p -> p.getServiceType().equals(ServiceType.PROJECT) || p.getServiceType().equals(ServiceType.PRECONFIGURED)).getSlug();
        }

        ResponseEntity<Void> response = restartRequest(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project restarting started...");
        }
    }

    public void start(String slug) {
        if (slug == null) {
            slug = select(p -> p.getInstances() == 0 && p.getRequiredInstances() > 0).getSlug();
        }

        ResponseEntity<Void> response = startRequest(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Starting project...");
        }
    }

    public void stop(String slug) {
        ProjectResponse project = findOrSelect(slug, p -> p.getInstances() > 0 && p.getRequiredInstances() > 0);

        ResponseEntity<Void> response = stopRequest(project.getSlug());

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
           response = cnpgService.scaleRequest(project.getSlug(), instances);
        } else {
           response = scaleRequest(project.getSlug(), instances);
        }


        if (response.getStatusCode().is2xxSuccessful()) {
            helper.println("Project scaled...");
        }
    }

    public void freeze(String slug) {
        if (slug != null) {
            ProjectResponse project = findBySlug(slug);

            if (!project.getServiceType().equals(ServiceType.PROJECT)) {
                throw new UnsupportedServiceTypeException("Unable to freeze not PROJECT type");
            }

            slug = project.getSlug();
        } else {
            slug = select(p -> p.getServiceType().equals(ServiceType.PROJECT)).getSlug();
        }

        ResponseEntity<Void> response = freezeRequest(slug);

        if (response.getStatusCode().is2xxSuccessful()) {
            helper.print(String.format("Project %s has been frozen", slug));
        }
    }

    public TariffResponse getTariffRequest(String slug) {
        ResponseEntity<TariffResponse> response = client.project().get()
                .uri("/{slug}/tariff", slug)
                .retrieve()
                .toEntity(TariffResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }

    public void updateTariffRequest(String slug, int tariffId) {
        ResponseEntity<String> response = client.project()
                .post().uri("/{slug}/tariff", slug)
                .body(tariffId)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Changing tariff failed.");
        }
    }

    public ResponseEntity<Void> deleteRequest(ProjectResponse project) {
        ResponseEntity<Void> response = switch (project.getServiceType()) {
            case ServiceType.PROJECT, ServiceType.PRECONFIGURED -> deleteRequest(project.getSlug());
            case ServiceType.POSTGRESQL -> cnpgService.deleteRequest(project.getSlug());
        };

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Deletion failed.");
        }

        return response;
    }


    public ResponseEntity<Void> deleteRequest(String slug) {
        return client.project().delete()
                .uri("/{slug}", slug)
                .retrieve().toBodilessEntity();
    }

    public ResponseEntity<Void> startRequest(String slug) {
        ResponseEntity<Void> response = client.project().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(1))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to start project.");
        }

        return response;
    }

    public ResponseEntity<Void> stopRequest(String slug) {
        ResponseEntity<Void> response = client.project().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(0))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to stop project.");
        }

        return response;
    }

    public ProjectResponse findBy(String name) {
        List<ProjectResponse> projects = this.getProjectListRequest();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        if (projects.isEmpty()) {
            throw ClientExceptions.noContent("Project was not found.");
        }

        return projects.getFirst();
    }

    public ProjectResponse findBySlug(String slug) {
        ResponseEntity<ProjectResponse> response = client.project()
                .get()
                .uri("/{slug}", slug)
                .retrieve()
                .toEntity(ProjectResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // todo throw not found
        }

        return response.getBody();
    }

    public ProjectResponse findOrSelect(String slug) {
        return slug == null ? select() : findBySlug(slug);
    }

    public ProjectResponse findOrSelect(String slug, Predicate<ProjectResponse> predicate) {
        return slug == null ? select(predicate) : findBySlug(slug);
    }

    public ProjectResponse select() {
        List<SelectorItem<ProjectSelectItem>> projectList = getProjectListRequest()
                .stream()
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select project: ", true).getProject();
    }

    public ProjectResponse select(Predicate<ProjectResponse> predicate) {
        List<SelectorItem<ProjectSelectItem>> projectList = getProjectListRequest()
                .stream()
                .filter(predicate)
                .map(ProjectResponse::toSelectorItem).toList();
        return selector.singleSelector(projectList, "Select project: ", true).getProject();
    }

    public ResponseEntity<Void> scaleRequest(String slug, Integer num) {
        ResponseEntity<Void> response = client.project().post()
                .uri("/{slug}/scale", slug)
                .body(new ScalePostRequest(num))
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Unable to change scale project.");
        }

        return response;
    }

    public List<ProjectResponse> getProjectListRequest() {
        ProjectListResponse projectList = client.project().get()
                .retrieve()
                .body(ProjectListResponse.class);

        if (projectList == null || projectList.getServices().isEmpty()) {
            throw ClientExceptions.noContent("Projects were not found.");
        }

        return projectList.getServices();
    }

    public ResponseEntity<ConfigGetResponse> getConfigRequest() {
        String token = tokenUtils.readToken().accessToken();

        return client.configurations(token).build().get()
                .retrieve()
                .toEntity(ConfigGetResponse.class);
    }

    public ResponseEntity<Void> freezeRequest(String slug) {
        ResponseEntity<Void> response = client.project().put()
                .uri("/{slug}/freeze", slug)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().isError()) {
            // todo: throw exception and handle it
            System.out.println("Freezing failed.");
        }

        return response;
    }

    public ResponseEntity<Void> rebuildRequest(String slug) {
        ResponseEntity<Void> response = client.project().post()
                .uri("/{slug}/rebuild", slug)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Rebuilding failed.");
        }

        return response;
    }

    public ResponseEntity<Void> restartRequest(String slug) {

        ResponseEntity<Void> response = client.project().post()
                .uri("/{slug}/restart", slug)
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Restarting failed.");
        }

        return response;
    }

    public ProjectPostResponse createProjectRequest(String name, Integer tariff) {
        ProjectPostResponse project = client.project().post()
                .body(new ProjectRequest(name, tariff))
                .retrieve()
                .body(ProjectPostResponse.class);

        if (project == null) {
            throw new RuntimeException("Project creation failed.");
        }

        return project;
    }

    public void addConfigRequest(AmveraConfiguration body, String slug) {
        ResponseEntity<String> response = client.project().post()
                .uri("/{slug}/config?slug={slug}", slug, slug)
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("Creating configuration failed.");
        }

        System.out.println("Config amvera.yml added");
    }
}
