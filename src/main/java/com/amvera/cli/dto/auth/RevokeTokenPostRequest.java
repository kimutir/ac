package com.amvera.cli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RevokeTokenPostRequest {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("token")
    private String token;
    @JsonProperty("token_type_hint")
    private String tokenTypeHint;

    public RevokeTokenPostRequest(String clientId, String refreshToken) {
        this.clientId = clientId;
        this.token = refreshToken.trim();
        this.tokenTypeHint = "refresh_token";
    }

    public MultiValueMap<String, String> toMultiValueMap() {

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("token", token);
        body.add("token_type_hint", tokenTypeHint);

        return body;

    }

}
