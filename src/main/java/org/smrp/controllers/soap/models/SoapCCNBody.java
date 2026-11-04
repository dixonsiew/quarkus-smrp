package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class SoapCCNBody {

    @XmlElement(name = "ListOfCountryNamesByNameResponse",
                namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private ListOfCountryNamesByNameResponse listOfCountryNamesByNameResponse;

    public ListOfCountryNamesByNameResponse getListOfCountryNamesByNameResponse() {
        return listOfCountryNamesByNameResponse;
    }

    public void setListOfCountryNamesByNameResponse(ListOfCountryNamesByNameResponse listOfCountryNamesByNameResponse) {
        this.listOfCountryNamesByNameResponse = listOfCountryNamesByNameResponse;
    }
}
