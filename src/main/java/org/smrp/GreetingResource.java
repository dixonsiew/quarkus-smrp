package org.smrp;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.dtos.LoginDto;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Tag(name = "Greeting")
@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public LoginDto login(@Valid LoginDto loginDto) {
        return loginDto;
    }
}
