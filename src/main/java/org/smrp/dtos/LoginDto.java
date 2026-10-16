package org.smrp.dtos;

import jakarta.validation.constraints.NotBlank;


public record LoginDto(

    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}
