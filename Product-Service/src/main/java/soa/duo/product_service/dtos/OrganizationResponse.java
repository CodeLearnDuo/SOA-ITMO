package soa.duo.product_service.dtos;

import soa.duo.product_service.model.enums.OrganizationType;

public record OrganizationResponse(
        Integer id,
        String name,
        Integer employeesCount,
        OrganizationType type
) {
}
