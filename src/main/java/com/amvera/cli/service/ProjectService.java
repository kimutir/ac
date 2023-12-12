package com.amvera.cli.service;

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

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public ProjectService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public List<ProjectResponse> getProjects()  {
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
        String url = "https://api.staging.amvera.ru/projects";
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ProjectRequest> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        ATest test = mapper.readValue(response.getBody(), ATest.class);
        return test;
    }

    public void createProject(AmveraConfiguration body, String slug) {
        String url = String.format("https://api.staging.amvera.ru/projects/%s/config?slug=%s", slug, slug);

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<AmveraConfiguration> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // todo: throw exception
        }

    }

}
