package blps.duo.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "UnitOfMeasure", namespace = "http://www.example.com/ebay")
@XmlEnum
public enum UnitOfMeasure {
    @XmlEnumValue("CENTIMETERS")
    CENTIMETERS("CENTIMETERS"),

    @XmlEnumValue("SQUARE_METERS")
    SQUARE_METERS("SQUARE_METERS"),

    @XmlEnumValue("PCS")
    PCS("PCS"),

    @XmlEnumValue("GRAMS")
    GRAMS("GRAMS");

    private final String value;

    UnitOfMeasure(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}