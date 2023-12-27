package com.amvera.cli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RegisterReflectionForBinding
public class RefreshTokenPostRequest {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("grant_type")
    private String grantType;

    public RefreshTokenPostRequest(String clientId, String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = "password";
        this.refreshToken = refreshToken.trim();
        this.grantType = "refresh_token";
    }

    public MultiValueMap<String, String> toMultiValueMap() {

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);
        body.add("client_secret", clientSecret);
        body.add("grant_type", grantType);

        return body;

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
