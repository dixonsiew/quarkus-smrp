package org.smrp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ChangePasswordDto(
    
    @NotBlank(message = "Password is required")
    @Size(max = 150, message = "Password must not exceed 150 characters")
    String password,

    @JsonProperty("confirm_password")
    @NotBlank(message = "Confirm password is required")
    @Size(max = 150, message = "Confirm password must not exceed 150 characters")
    String confirmPassword
) {}
