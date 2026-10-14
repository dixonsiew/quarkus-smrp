package org.smrp.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;


@Singleton 
public class PrettyPrintCustomizer implements ObjectMapperCustomizer {
    
    @Override
    public void customize(ObjectMapper mapper) {
        // Enables pretty printing for all serialized responses
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
