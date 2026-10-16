package org.smrp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CommonSetupDto(

    @NotBlank(message = "Code is required")
    @Size(max = 30, message = "Code must not exceed 30 characters")
    String code,

    @NotBlank(message = "Desc is required")
    @Size(max = 300, message = "Desc must not exceed 300 characters")
    String desc,

    @NotBlank(message = "Ref is required")
    @Size(max = 200, message = "Ref must not exceed 200 characters")
    String ref
) {}
