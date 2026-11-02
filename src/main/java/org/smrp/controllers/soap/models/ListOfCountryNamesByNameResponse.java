package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfCountryNamesByNameResponse {

    @XmlElement(name = "ListOfCountryNamesByNameResult",
                namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private ListOfCountryNamesByNameResult listOfCountryNamesByNameResult;

    public ListOfCountryNamesByNameResult getListOfCountryNamesByNameResult() {
        return listOfCountryNamesByNameResult;
    }

    public void setListOfCountryNamesByNameResult(ListOfCountryNamesByNameResult listOfCountryNamesByNameResult) {
        this.listOfCountryNamesByNameResult = listOfCountryNamesByNameResult;
    }
}
