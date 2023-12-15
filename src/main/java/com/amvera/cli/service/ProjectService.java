package com.amvera.cli.service;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ProjectService {

    private final Endpoints endpoints;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public ProjectService(Endpoints endpoints, RestTemplate restTemplate, ObjectMapper mapper) {
        this.endpoints = endpoints;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public List<ProjectResponse> getProjects() {
        String token = TokenUtils.readResponseToken();
        HttpHeaders projectsHeaders = new HttpHeaders();
        projectsHeaders.setContentType(MediaType.APPLICATION_JSON);
        projectsHeaders.setBearerAuth(token);
        HttpEntity<Object> projectsEntity = new HttpEntity<>(projectsHeaders);
        ResponseEntity<String> projects = restTemplate.exchange(endpoints.projects(), HttpMethod.GET, projectsEntity, String.class);
        ProjectListResponse projectList;

        try {
            projectList = mapper.readValue(projects.getBody(), ProjectListResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return projectList.getServices();
    }

    public ATest addConfig(String name, Integer tariff) throws JsonProcessingException {
        ProjectRequest body = new ProjectRequest(name, tariff);
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ProjectRequest> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoints.projects(), entity, String.class);
        ATest test = mapper.readValue(response.getBody(), ATest.class);
        return test;
    }

    public String addConfig(AmveraConfiguration body, String slug) {
        String url = String.format(endpoints.projects() + "/%s/config?slug=%s", slug, slug);

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<AmveraConfiguration> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project created";
    }

    public String rebuild(String project) {
        ProjectResponse projectResponse = findBy(project);
        String url = String.format(endpoints.projects() + "/%s/rebuild", projectResponse.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project rebuilding started...";
    }

    public String restart(String p) {
        ProjectResponse project = findBy(p);
        String url = String.format(endpoints.projects() + "/%s/restart", project.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project restarting started...";
    }

    public String delete(String project) {
        ProjectResponse projectResponse = findBy(project);
        String url = String.format(endpoints.projects() + "/%s", projectResponse.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project deleted successfully!";
    }


    public String start(String project) {
        ProjectResponse projectResponse = findBy(project);
        String url = String.format(endpoints.projects() + "/%s/scale", projectResponse.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ScalePostRequest> entity = new HttpEntity<>(new ScalePostRequest(1), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project started!";
    }

    public String stop(String project) {
        ProjectResponse projectResponse = findBy(project);
        String url = String.format(endpoints.projects() + "/%s/scale", projectResponse.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ScalePostRequest> entity = new HttpEntity<>(new ScalePostRequest(0), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        return "Project stopped!";
    }

    public List<LogGetResponse> logs(String p, String type, int limit) {
        ProjectResponse project = findBy(p);
        String url = String.format(endpoints.logs() + "/%s/history?username=%s&serviceName=%s&limit=%d", type, project.getOwnerName(), project.getSlug(), limit);

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        List<LogGetResponse> logs;

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

        try {
            logs = mapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return logs;
    }


    public TariffGetResponse getTariff(String slug) {
        String url = String.format(endpoints.projects() + "/%s/tariff", slug);
        String token = TokenUtils.readResponseToken();
        HttpHeaders projectsHeaders = new HttpHeaders();
        projectsHeaders.setContentType(MediaType.APPLICATION_JSON);
        projectsHeaders.setBearerAuth(token);
        HttpEntity<Object> projectsEntity = new HttpEntity<>(projectsHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, projectsEntity, String.class);
        TariffGetResponse tariff;

        try {
            tariff = mapper.readValue(response.getBody(), TariffGetResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return tariff;
    }

    public ProjectResponse findBy(String name) {
        List<ProjectResponse> projects = this.getProjects();
        projects = projects.stream()
                .filter(p -> String.valueOf(p.getId()).equals(name) || p.getName().equals(name) || p.getSlug().equals(name))
                .toList();

        //todo: throw exception
        if (projects.isEmpty()) return null;

        return projects.getFirst();
    }

    public void changeTariff(String slug, int tariffId) {
        String url = String.format(endpoints.projects() + "/%s/tariff", slug);
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Integer> entity = new HttpEntity<>(tariffId, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().value() != 200) {
            // todo: throw exception
        }

    }

    public void changeScale(Integer num) {

    }


}
