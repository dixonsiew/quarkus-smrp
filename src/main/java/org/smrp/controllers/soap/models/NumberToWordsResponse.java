package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class NumberToWordsResponse {
    
    @XmlElement(name = "NumberToWordsResult",
                namespace = "http://www.dataaccess.com/webservicesserver/",
                required = true)
    private String numberToWordsResult;

    public String getNumberToWordsResult() {
        return numberToWordsResult;
    }

    public void setNumberToWordsResult(String numberToWordsResult) {
        this.numberToWordsResult = numberToWordsResult;
    }
}
