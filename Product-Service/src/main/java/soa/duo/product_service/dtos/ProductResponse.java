package soa.duo.product_service.dtos;

import soa.duo.product_service.model.Coordinates;
import soa.duo.product_service.model.enums.UnitOfMeasure;

import java.time.LocalDateTime;

public record ProductResponse(
        Integer id,
        String name,
        Coordinates coordinates,
        LocalDateTime creationDate,
        Double price,
        String partNumber,
        UnitOfMeasure unitOfMeasure,
        OrganizationResponse manufacturer
) {}

