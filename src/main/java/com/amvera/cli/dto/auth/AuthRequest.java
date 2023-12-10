package com.amvera.cli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthRequest {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("grant_type")
    private String grantType;

    public AuthRequest(String clientId, String username, String password) {
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.grantType = "password";
    }

    public MultiValueMap<String, String> toMultiValueMap() {

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grantType);

        return body;

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
