package org.smrp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record UserDto(

    @NotBlank(message = "Username is required")
    @Size(max = 150, message = "Username must not exceed 150 characters")
    String username,

    @NotBlank(message = "Password is required")
    @Size(max = 150, message = "Password must not exceed 150 characters")
    String password,

    @JsonProperty("first_name")
    @NotBlank(message = "Firstname is required")
    @Size(max = 150, message = "Firstname must not exceed 150 characters")
    String firstname,

    @JsonProperty("last_name")
    @Size(max = 150, message = "Lastname must not exceed 150 characters")
    String lastname,

    @JsonProperty("role_id")
    @Positive
    Long roleId
) {}
