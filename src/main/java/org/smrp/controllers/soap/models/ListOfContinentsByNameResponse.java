package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfContinentsByNameResponse {
    
    @XmlElement(name = "ListOfContinentsByNameResult",
                namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private ListOfContinentsByNameResult listOfContinentsByNameResult;

    public ListOfContinentsByNameResult getListOfContinentsByNameResult() {
        return listOfContinentsByNameResult;
    }

    public void setListOfContinentsByNameResult(ListOfContinentsByNameResult listOfContinentsByNameResult) {
        this.listOfContinentsByNameResult = listOfContinentsByNameResult;
    }
}
