package org.smrp;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import jakarta.ws.rs.core.Application;


@OpenAPIDefinition(
    info = @Info(
        title = "Swagger SMRP Backend API",
        version = "1.0.0",
        description = "This API handles all SMRP operations."
    ),
    components = @Components(
        securitySchemes = {
            @SecurityScheme(
                securitySchemeName = "BearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
            )
        }
    )
)
public class SwaggerConfig extends Application {
    
}
