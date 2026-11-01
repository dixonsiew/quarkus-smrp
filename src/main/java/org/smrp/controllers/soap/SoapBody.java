package org.smrp.controllers.soap;

import jakarta.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class SoapBody {

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
