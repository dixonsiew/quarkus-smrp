package org.smrp.controllers.soap;

import jakarta.xml.bind.annotation.*;


@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapEnvelope {

    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private SoapBody body;

    public SoapBody getBody() {
        return body;
    }

    public void setBody(SoapBody body) {
        this.body = body;
    }
}
