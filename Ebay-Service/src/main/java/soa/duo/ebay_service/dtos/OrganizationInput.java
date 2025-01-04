package soa.duo.ebay_service.dtos;

import soa.duo.ebay_service.model.enums.OrganizationType;

public record OrganizationInput(
        String name,
        int employeesCount,
        OrganizationType type
) {
}