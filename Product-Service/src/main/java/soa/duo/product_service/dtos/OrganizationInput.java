package soa.duo.product_service.dtos;

import soa.duo.product_service.model.enums.OrganizationType;

public record OrganizationInput(
        String name,
        int employeesCount,
        OrganizationType type
) {
}
