package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.config.ConfigResponse;
import com.amvera.cli.exception.ResourceNotFoundException;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class ConfigClient extends BaseHttpClient {

    public ConfigClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.configurations(), tokenUtils.readToken().accessToken());
    }

    public ConfigResponse get() {
        return client().get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    throw new ResourceNotFoundException(status + ". Unable to get balance");
                })
                .body(ConfigResponse.class);
    }

}
