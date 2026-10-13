package org.smrp.models;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ValidationExceptionMapper {
    
    @ServerExceptionMapper
    public Response handleConstraintViolation(ConstraintViolationException exception) {
        List<CustomErrorResponse.FieldError> fieldErrors = exception.getConstraintViolations()
            .stream()
            .map(violation -> new CustomErrorResponse.FieldError(
                violation.getPropertyPath().toString(), // e.g., "createProductDTO.name"
                violation.getMessage()
            ))
            .collect(Collectors.toList());

        // CustomErrorResponse errorPayload = new CustomErrorResponse("Validation Failed", fieldErrors);

        List<String> lm = new ArrayList<String>();
        for (CustomErrorResponse.FieldError fieldError : fieldErrors) {
            lm.add(fieldError.getMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), String.join(" and ", lm));

        // Return a 400 Bad Request or 422 Unprocessable Entity depending on API design
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
