package com.amvera.cli.client;

import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface KeycloakAuthClient {
    @PostExchange(url = "/auth/realms/amvera/protocol/openid-connect/token"
            , contentType = "application/x-www-form-urlencoded"
    )
    Mono<AuthResponse> login(@RequestBody MultiValueMap<String,String> body);

}
