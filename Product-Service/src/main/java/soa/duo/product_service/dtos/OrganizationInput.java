package soa.duo.product_service.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import soa.duo.product_service.config.CaseInsensitiveEnumDeserializer;
import soa.duo.product_service.model.enums.OrganizationType;

public record OrganizationInput(
        String name,
        int employeesCount,
        @JsonDeserialize(using = CaseInsensitiveEnumDeserializer.class)
        OrganizationType type
) {
}
