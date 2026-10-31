package org.smrp.controllers.soap;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.WebClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Soap")
@Path("/soap")
public class SoapResource {
    
    private final WebClient webClient;

    public SoapResource(Vertx vertx) {
        WebClientOptions options = new WebClientOptions()
                .setMaxPoolSize(50)
                .setConnectTimeout(5000);
        this.webClient = WebClient.create(vertx, options);
    }

    @GET
    @Path("/countries")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> getCountries() {
        String s = """
            <x:Envelope
                xmlns:x="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:cou="https://soap-service-free.mock.beeceptor.com/CountryInfoService">
                <x:Header/>
                <x:Body>
                    <cou:ListOfCountryNamesByName/>
                </x:Body>
            </x:Envelope>
        """;
        Uni<String> data = webClient.postAbs("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso")
            .putHeader("Content-Type", "text/xml; charset=utf-8")
            .putHeader("SOAPAction", "https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso/ListOfCountryNamesByName")
            .sendBuffer(Buffer.buffer(s))
            .onItem().transform(HttpResponse::bodyAsString);
        return data.flatMap(m -> {
            return Uni.createFrom().item(Response.ok(m).build());
        });
    }
}
