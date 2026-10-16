package org.smrp.controllers.auth;

import java.util.HashMap;

import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.dtos.ChangePasswordDto;
import org.smrp.filters.Auth;
import org.smrp.services.UserService;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Auth")
@SecurityRequirement(name = "BearerAuth")
@Path("/api")
@Auth
public class CurrentUserResource {
    
    private final SecurityIdentity securityIdentity;
    private final UserService userService;
    
    public CurrentUserResource(SecurityIdentity securityIdentity, UserService userService) {
        this.securityIdentity = securityIdentity;
        this.userService = userService;
    }

    @GET 
    @Path("/current-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> userDetails() {
        String username = securityIdentity.getPrincipal().getName();
        return userService.findByUsername(username).flatMap(user -> {
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            HashMap<String, Object> mx = new HashMap<>();
            mx.put("id", user.getId());
            mx.put("username", user.getUsername());
            mx.put("first_name", user.getFirstName());
            mx.put("last_name", user.getLastName());
            mx.put("roles", user.getRoles());
            return Uni.createFrom().item(Response.ok(mx).build());
        });
    }

    @POST
    @Path("/change-password")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> changePassword(@Valid ChangePasswordDto data) {
        if (data.password() != data.confirmPassword()) {
            throw new BadRequestException("Confirm Password does not match");
        }

        String username = securityIdentity.getPrincipal().getName();
        return userService.findByUsername(username).flatMap(user -> {
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            user.setPassword(data.password());
            return userService.updatePassword(user).flatMap(x -> {
                HashMap<String, Object> mx = new HashMap<>();
                mx.put("success", 1);
                return Uni.createFrom().item(Response.ok(mx).build());
            });
        });
    }
}
