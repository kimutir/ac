package com.amvera.cli.service;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BalanceService {
    private final RestTemplate restTemplate;
    private final Endpoints endpoints;
    private final ObjectMapper mapper;

    public BalanceService(RestTemplate restTemplate, Endpoints endpoints, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.endpoints = endpoints;
        this.mapper = mapper;
    }

    public BalanceGetResponse getBalance() {
        String token = TokenUtils.readResponseToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(endpoints.balance(), HttpMethod.GET, entity, String.class);

        try {
            return mapper.readValue(response.getBody(), BalanceGetResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
