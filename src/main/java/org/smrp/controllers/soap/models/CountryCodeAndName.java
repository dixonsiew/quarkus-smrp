package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class CountryCodeAndName {

    @XmlElement(name = "sISOCode",
                namespace = "http://www.oorsprong.org/websamples.countryinfo",
                required = true)
    private String isoCode;

    @XmlElement(name = "sName",
                namespace = "http://www.oorsprong.org/websamples.countryinfo",
                required = true)
    private String name;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CountryCodeAndName{isoCode='" + isoCode + "', name='" + name + "'}";
    }
}
