package soa.duo.product_service.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import soa.duo.product_service.config.CaseInsensitiveEnumDeserializer;
import soa.duo.product_service.model.Coordinates;
import soa.duo.product_service.model.enums.UnitOfMeasure;

public record ProductInput(
        String name,
        Coordinates coordinates,
        Double price,
        String partNumber,
        @JsonDeserialize(using = CaseInsensitiveEnumDeserializer.class)
        UnitOfMeasure unitOfMeasure,
        OrganizationInput manufacturer
) {
}
