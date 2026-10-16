package org.smrp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;


public record RefreshTokenDto(

    @JsonProperty("refresh_token")
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}
