package com.amvera.cli.service;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.TariffGetResponse;
import com.amvera.cli.dto.project.ATest;
import com.amvera.cli.dto.project.ProjectListResponse;
import com.amvera.cli.dto.project.ProjectRequest;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.config.AmveraConfiguration;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        String projectsUrl = "https://api.staging.amvera.ru/projects";
        String token = TokenUtils.readResponseToken();
        HttpHeaders projectsHeaders = new HttpHeaders();
        projectsHeaders.setContentType(MediaType.APPLICATION_JSON);
        projectsHeaders.setBearerAuth(token);
        HttpEntity<Object> projectsEntity = new HttpEntity<>(projectsHeaders);
        ResponseEntity<String> projects = restTemplate.exchange(projectsUrl, HttpMethod.GET, projectsEntity, String.class);
        ProjectListResponse projectList;

        try {
            projectList = mapper.readValue(projects.getBody(), ProjectListResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return projectList.getServices();
    }

    public ATest createProject(String name, Integer tariff) throws JsonProcessingException {
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

    public String createProject(AmveraConfiguration body, String slug) {
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

}
