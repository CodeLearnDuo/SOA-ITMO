package soa.duo.ebay_service.dtos;

import soa.duo.ebay_service.model.Coordinates;
import soa.duo.ebay_service.model.enums.UnitOfMeasure;

public record ProductInput(
        String name,
        Coordinates coordinates,
        double price,
        String partNumber,
        UnitOfMeasure unitOfMeasure,
        OrganizationInput manufacturer
) {}
