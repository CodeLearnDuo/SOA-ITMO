package blps.duo.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "OrganizationType", namespace = "http://www.example.com/ebay")
@XmlEnum
public enum OrganizationType {
    @XmlEnumValue("COMMERCIAL")
    COMMERCIAL("COMMERCIAL"),

    @XmlEnumValue("PUBLIC")
    PUBLIC("PUBLIC"),

    @XmlEnumValue("GOVERNMENT")
    GOVERNMENT("GOVERNMENT"),

    @XmlEnumValue("PRIVATE_LIMITED_COMPANY")
    PRIVATE_LIMITED_COMPANY("PRIVATE_LIMITED_COMPANY"),

    @XmlEnumValue("OPEN_JOINT_STOCK_COMPANY")
    OPEN_JOINT_STOCK_COMPANY("OPEN_JOINT_STOCK_COMPANY");

    private final String value;

    OrganizationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}