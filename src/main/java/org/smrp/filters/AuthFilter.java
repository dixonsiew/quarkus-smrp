package org.smrp.filters;

import java.util.Map;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.smrp.models.ErrorResponse;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;


public class AuthFilter {
    
    @ServerRequestFilter
    @Auth
    public Response filterRequests(ContainerRequestContext context) {
        Map<String, Cookie> cookies = context.getCookies();
        Cookie tokenCookie = cookies.get("token");
        String token = "";
        if (tokenCookie != null) {
            token = tokenCookie.getValue();
        }

        if (token.isEmpty()) {
            String authHeader = context.getHeaderString("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            }
        }
         
        if (token.isEmpty()) {
            ErrorResponse error = new ErrorResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
        
        // Return null to allow the request to proceed downstream
        return null; 
    }
}
