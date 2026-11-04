package org.smrp.controllers.soap.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class Continent {
    
    @XmlElement(name = "sCode",
                namespace = "http://www.oorsprong.org/websamples.countryinfo",
                required = true)
    private String code;

    @XmlElement(name = "sName",
                namespace = "http://www.oorsprong.org/websamples.countryinfo",
                required = true)
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Continent{code='" + code + "', name='" + name + "'}";
    }
}
