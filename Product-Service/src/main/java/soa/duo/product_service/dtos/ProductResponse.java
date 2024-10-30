package soa.duo.product_service.dtos;

import soa.duo.product_service.model.Coordinates;
import soa.duo.product_service.model.enums.UnitOfMeasure;

import java.time.LocalDateTime;

public record ProductResponse(
        String name,
        Coordinates coordinates,
        LocalDateTime creationDate,
        double price,
        String partNumber,
        UnitOfMeasure unitOfMeasure,
        OrganizationResponse manufacturer
){
}
