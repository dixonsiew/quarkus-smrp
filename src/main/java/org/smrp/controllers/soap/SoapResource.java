package org.smrp.controllers.soap;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.controllers.soap.models.Continent;
import org.smrp.controllers.soap.models.CountryCodeAndName;
import org.smrp.services.SoapService;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Soap")
@Path("/soap")
public class SoapResource {
    
    private final SoapService soapService;

    public SoapResource(SoapService soapService) {
        this.soapService = soapService;
    }

    @APIResponse(
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CountryCodeAndName[].class) // Key: use array class
        )
    )
    @GET
    @Path("/countryCodeAndNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCountryCodeAndNames() {
        return soapService.getCountryCodeAndNames();
    }

    @APIResponse(
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Continent[].class) // Key: use array class
        )
    )
    @GET
    @Path("/continents")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getContinents() {
        return soapService.getContinents();
    }

    @GET
    @Path("/numberToWords/{number}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> numberToWords(@PathParam("number") String number) {
        return soapService.getNumberToWords(number);
    }
}
