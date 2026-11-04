package org.smrp.services;

import java.io.StringReader;
import java.util.List;

import org.smrp.controllers.soap.models.Continent;
import org.smrp.controllers.soap.models.CountryCodeAndName;
import org.smrp.controllers.soap.models.SoapCCNEnvelope;
import org.smrp.controllers.soap.models.SoapContinentEnvelope;
import org.smrp.controllers.soap.models.SoapN2WEnvelope;

import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;


@ApplicationScoped
public class SoapService {
    
    private final WebClient webClient;
    private final JAXBContext contextCCN;
    private final JAXBContext contextContinent;
    private final JAXBContext contextNumberToWords;

    public SoapService(Vertx vertx) {
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(false)
                .setMaxPoolSize(50)
                .setConnectTimeout(10000);
        this.webClient = WebClient.create(vertx, options);
        try {
            this.contextCCN = JAXBContext.newInstance(SoapCCNEnvelope.class);
            this.contextContinent = JAXBContext.newInstance(SoapContinentEnvelope.class);
            this.contextNumberToWords = JAXBContext.newInstance(SoapN2WEnvelope.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JAXBContext", e);
        }
    }

    public Uni<Response> getCountryCodeAndNames() {
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
                Unmarshaller unmarshaller = contextCCN.createUnmarshaller();
                SoapCCNEnvelope envelope = (SoapCCNEnvelope) unmarshaller.unmarshal(new StringReader(m));
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

    public Uni<Response> getContinents() {
        String s = """
            <x:Envelope
                xmlns:x="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:cou="https://soap-service-free.mock.beeceptor.com/CountryInfoService">
                <x:Header/>
                <x:Body>
                    <cou:ListOfContinentsByName/>
                </x:Body>
            </x:Envelope>
        """;
        Uni<String> data = webClient.postAbs("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso")
            .putHeader("Content-Type", "text/xml; charset=utf-8")
            .putHeader("SOAPAction", "https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso/ListOfContinentsByName")
            .sendBuffer(Buffer.buffer(s))
            .onItem().transform(HttpResponse::bodyAsString);
        return data.flatMap(m -> {
            try {
                Unmarshaller unmarshaller = contextContinent.createUnmarshaller();
                SoapContinentEnvelope envelope = (SoapContinentEnvelope) unmarshaller.unmarshal(new StringReader(m));
                List<Continent> result = envelope.getBody()
                    .getListOfContinentsByNameResponse()
                    .getListOfContinentsByNameResult()
                    .getContinents();
                return Uni.createFrom().item(Response.ok(result).build());
            } catch (Exception e) {
                return Uni.createFrom().failure(e);
            }
        });
    }

    public Uni<Response> getNumberToWords(String number) {
        String s = """
            <x:Envelope
                xmlns:x="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:web="http://www.dataaccess.com/webservicesserver/">
                <x:Header/>
                <x:Body>
                    <web:NumberToWords>
                        <web:ubiNum>%s</web:ubiNum>
                    </web:NumberToWords>
                </x:Body>
            </x:Envelope>
        """.formatted(number);
        Uni<String> data = webClient.postAbs("https://www.dataaccess.com/webservicesserver/numberconversion.wso")
            .putHeader("Content-Type", "text/xml; charset=utf-8")
            .putHeader("SOAPAction", "")
            .sendBuffer(Buffer.buffer(s))
            .onItem().transform(HttpResponse::bodyAsString);
        return data.flatMap(m -> {
            try {
                System.out.println("Response: " + m);
                Unmarshaller unmarshaller = contextNumberToWords.createUnmarshaller();
                SoapN2WEnvelope envelope = (SoapN2WEnvelope) unmarshaller.unmarshal(new StringReader(m));
                String result = envelope.getBody()
                    .getNumberToWordsResponse()
                    .getNumberToWordsResult();
                return Uni.createFrom().item(Response.ok(result).build());
            } catch (Exception e) {
                return Uni.createFrom().failure(e);
            }
        }).onFailure().recoverWithItem(e -> {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error processing SOAP request: " + e.getMessage())
                .build();
        });
    }
}
