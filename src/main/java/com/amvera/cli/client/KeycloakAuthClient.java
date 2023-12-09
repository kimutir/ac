package com.amvera.cli.client;

import com.amvera.cli.dto.auth.AuthResponse;
import org.springframework.web.service.annotation.PostExchange;

public interface KeycloakAuthClient {

//    @GetExchange
    @PostExchange(url = "/auth/realms/amvera/protocol/openid-connect/token", contentType = "")
    AuthResponse login();

}
