package com.amvera.cli.client;

import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.project.config.ConfigResponse;
import com.amvera.cli.utils.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfigClient extends HttpClientAbs {

    public ConfigClient(Endpoints endpoints, TokenUtils tokenUtils) {
        super(endpoints.configurations(), tokenUtils.readToken().accessToken());
    }

    public ResponseEntity<ConfigResponse> get() {

        return client().get()
                .retrieve()
                .toEntity(ConfigResponse.class);
    }

}
