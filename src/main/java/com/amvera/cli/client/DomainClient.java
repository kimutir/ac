package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.exception.ClientResponseException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainClient extends BaseHttpClient {

    public DomainClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.domain(), tokenUtils.readToken().accessToken());
    }

    public List<DomainResponse> get(String slug) {
        return client()
                .get().uri("/{slug}", slug).retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                    String msg = "Error when getting project domain list";
                    throw new ClientResponseException(msg, status);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
