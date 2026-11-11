package org.smrp.services;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.smrp.controllers.kafka.models.Quote;

import io.quarkus.logging.Log;


@ApplicationScoped
public class PriceConsumer {
    
    @Incoming("prices-in")
    public void consume(double price) {
        Log.infof("Received price event from Kafka: $%.2f", price);
        // Add your business logic here (e.g., save to DB, alert system)
    }

    @Incoming("quotes")
    public void consumeQuote(Quote quote) {
        Log.infof("Received quote event from Kafka: %s", quote);
    }
}
