package org.smrp.services;

import java.io.StringReader;
import java.util.List;

import org.smrp.controllers.soap.models.CountryCodeAndName;
import org.smrp.controllers.soap.models.SoapEnvelope;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;


@ApplicationScoped
public class SoapService {
    
    private final WebClient webClient;
    private final JAXBContext context;

    public SoapService(Vertx vertx) {
        WebClientOptions options = new WebClientOptions()
                .setMaxPoolSize(50)
                .setConnectTimeout(5000);
        this.webClient = WebClient.create(vertx, options);
        try {
            this.context = JAXBContext.newInstance(SoapEnvelope.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JAXBContext", e);
        }
    }

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
            try {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                SoapEnvelope envelope = (SoapEnvelope) unmarshaller.unmarshal(new StringReader(m));
                List<CountryCodeAndName> result = envelope.getBody()
                    .getListOfCountryNamesByNameResponse()
                    .getListOfCountryNamesByNameResult()
                    .getCountryCodeAndNames();
                return Uni.createFrom().item(Response.ok(result).build());
            } catch (Exception e) {
                return Uni.createFrom().failure(e);
            }
        });
    }
}
