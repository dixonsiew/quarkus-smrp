package org.smrp.models;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.quarkus.logging.Log;
import io.quarkus.security.UnauthorizedException;


public class GlobalExceptionMapper {
    
    @ServerExceptionMapper
    public Response handleException(IllegalArgumentException exception) {
        ErrorResponse error = new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }

    @ServerExceptionMapper
    public Response handleRuntimeException(RuntimeException exception) {
        Log.error("RuntimeException: " + exception.getMessage(), exception);
        ErrorResponse error = new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getMessage());
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }

    @ServerExceptionMapper 
    public Response handleBadRequestException(BadRequestException exception) {
        ErrorResponse error = new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }

    @ServerExceptionMapper
    public Response handleUnauthorizedException(UnauthorizedException exception) {
        ErrorResponse error = new ErrorResponse(Response.Status.UNAUTHORIZED.getStatusCode(), exception.getMessage());
        
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(error)
                .build();
    }

    @ServerExceptionMapper
    public Response handleNotFoundException(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), exception.getMessage());
        
        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}
