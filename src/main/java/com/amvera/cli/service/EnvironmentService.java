package com.amvera.cli.service;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.*;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EnvironmentService {
    private final Endpoints endpoints;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final ProjectService projectService;

    public EnvironmentService(Endpoints endpoints, RestTemplate restTemplate, ObjectMapper mapper, ProjectService projectService) {
        this.endpoints = endpoints;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.projectService = projectService;
    }

    public List<EnvDTO> getEnvironment(String project) {
        ProjectResponse projectResponse = projectService.findBy(project);

        String url = String.format(endpoints.env() + "/%s", projectResponse.getSlug());

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        EnvListGetResponse envs;
        try {
            envs = mapper.readValue(response.getBody(), EnvListGetResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return envs.environmentVariables();
    }

    public void addEnvironment(EnvPostRequest env, String slug) {
        String url = String.format(endpoints.env() + "/%s", slug);
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<EnvPostRequest> entity = new HttpEntity<>(env, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public void updateEnvironment(EnvPutRequest env, String slug) {
        String url = String.format(endpoints.env() + "/%s/%d", slug, env.id());
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<EnvPutRequest> entity = new HttpEntity<>(env, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public void deleteEnvironment(Integer id, String slug) {
        String url = String.format(endpoints.env() + "/%s/%d", slug, id);
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Integer> entity = new HttpEntity<>(id, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        //todo: throw exception
        if (response.getStatusCode().value() != 200) return;
    }

    public List<EnvDTO> getEnvironmentBySlug(String slug) {

        String url = String.format(endpoints.env() + "/%s", slug);

        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        EnvListGetResponse envs;
        try {
            envs = mapper.readValue(response.getBody(), EnvListGetResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return envs.environmentVariables();
    }

}
