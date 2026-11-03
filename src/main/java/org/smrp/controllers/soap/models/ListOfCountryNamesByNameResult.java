package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfCountryNamesByNameResult {

    @XmlElement(name = "tCountryCodeAndName",
                namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private List<CountryCodeAndName> countryCodeAndNames = new ArrayList<>();

    public List<CountryCodeAndName> getCountryCodeAndNames() {
        return countryCodeAndNames;
    }

    public void setCountryCodeAndNames(List<CountryCodeAndName> countryCodeAndNames) {
        this.countryCodeAndNames = countryCodeAndNames;
    }
}
