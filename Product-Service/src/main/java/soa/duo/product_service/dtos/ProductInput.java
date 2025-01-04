package soa.duo.product_service.dtos;

import soa.duo.product_service.model.Coordinates;
import soa.duo.product_service.model.enums.UnitOfMeasure;

public record ProductInput(
        String name,
        Coordinates coordinates,
        Double price,
        String partNumber,
        UnitOfMeasure unitOfMeasure,
        OrganizationInput manufacturer
) {
}
