package org.smrp.controllers.kafka.models;

import java.util.Random;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped 
public class QuotesProcessor {

    private final Random random = new Random();

    @Incoming("requests") 
    @Outgoing("quotes")   
    @Blocking             
    public Quote process(String quoteRequest) throws InterruptedException {
        // simulate some hard working task
        Thread.sleep(200);
        return new Quote(quoteRequest, random.nextInt(100));
    }
}
