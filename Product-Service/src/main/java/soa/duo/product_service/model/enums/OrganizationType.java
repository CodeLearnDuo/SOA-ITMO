package soa.duo.product_service.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    @JsonCreator
    public static OrganizationType fromString(String value) {
        for (OrganizationType type : OrganizationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value for OrganizationType: " + value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
