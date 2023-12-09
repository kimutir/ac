package com.amvera.cli.client;

import com.amvera.cli.dto.auth.AuthRequest;
import com.amvera.cli.dto.auth.AuthResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface KeycloakAuthClient {
    @PostExchange(url = "/auth/realms/amvera/protocol/openid-connect/token", contentType = "application/x-www-form-urlencoded")
    AuthResponse login(@RequestBody AuthRequest body);

}
