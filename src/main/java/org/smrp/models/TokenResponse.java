package org.smrp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
    
    private String type;
    private String token;

    @JsonProperty("refresh_token")
    private String refreshToken;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
