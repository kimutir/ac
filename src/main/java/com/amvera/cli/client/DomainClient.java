package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainClient extends BaseHttpClient {

    public DomainClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.domain(), tokenUtils.readToken().accessToken());
    }

    public List<DomainResponse> get(String slug) {
        ResponseEntity<List<DomainResponse>> domainResponse = client()
                .get().uri("/{slug}", slug).retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return domainResponse.getBody();
    }

}
