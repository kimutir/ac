package com.amvera.cli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record LogoutRequest (
        @JsonProperty("client_id")
         String clientId,
        @JsonProperty("access_token")
         String accessToken
) {

    public MultiValueMap<String, String> toMultiValueMap() {

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_idss", clientId);
        body.add("access_token", accessToken);

        return body;

    }

}
