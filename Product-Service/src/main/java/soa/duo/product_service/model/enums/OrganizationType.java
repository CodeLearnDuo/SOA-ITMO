package soa.duo.product_service.model.enums;

public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    public static OrganizationType fromString(String value) {
        for (OrganizationType unit : OrganizationType.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid UnitOfMeasure: " + value);
    }
}
