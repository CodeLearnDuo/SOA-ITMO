package soa.duo.product_service.model.enums;

public enum UnitOfMeasure {
    CENTIMETERS,
    SQUARE_METERS,
    PCS,
    GRAMS;

    public static UnitOfMeasure fromString(String value) {
        for (UnitOfMeasure unit : UnitOfMeasure.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid UnitOfMeasure: " + value);
    }
}
