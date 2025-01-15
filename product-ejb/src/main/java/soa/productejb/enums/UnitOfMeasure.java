package soa.productejb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UnitOfMeasure {
    CENTIMETERS,
    SQUARE_METERS,
    PCS,
    GRAMS;

    @JsonCreator
    public static UnitOfMeasure fromString(String value) {
        for (UnitOfMeasure unit : UnitOfMeasure.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid value for UnitOfMeasure: " + value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}