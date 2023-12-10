package com.amvera.cli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
public class AuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("refresh_expires_in")
    private String refreshExpiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("session_state")
    private String sessionState;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("not-before-policy")
    private String notBeforePolicy;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(String refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getNotBeforePolicy() {
        return notBeforePolicy;
    }

    public void setNotBeforePolicy(String notBeforePolicy) {
        this.notBeforePolicy = notBeforePolicy;
    }
}
