package org.smrp.controllers.kafka;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.smrp.controllers.kafka.models.Quote;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Kafka")
@Path("/kafka")
public class KafkaResource {
    
    private final Emitter<Double> priceEmitter;
    private final Emitter<String> quoteRequestEmitter;
    private final Multi<Quote> quotes;

    public KafkaResource(
        @Channel("market-prices") Emitter<Double> priceEmitter, 
        @Channel("quote-requests") Emitter<String> quoteRequestEmitter,
        @Channel("quotes") Multi<Quote> quotes
    ) {
        this.priceEmitter = priceEmitter;
        this.quoteRequestEmitter = quoteRequestEmitter;
        this.quotes = quotes;
    }

    @POST
    @Path("/{price}")
    public Response sendPrice(@PathParam("price") double price) {
        priceEmitter.send(price);
        return Response.accepted().build();
    }

    @POST
    @Path("/quotes/request")
    @Produces(MediaType.TEXT_PLAIN)
    public String createRequest() {
        UUID uuid = UUID.randomUUID();
        quoteRequestEmitter.send(uuid.toString()); 
        return uuid.toString(); 
    }

    @GET
    @Path("/quotes/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Multi<Quote> stream() {
        return quotes; 
    }
}
