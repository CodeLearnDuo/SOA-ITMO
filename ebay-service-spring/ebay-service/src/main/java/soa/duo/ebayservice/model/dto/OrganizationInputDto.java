package soa.duo.ebayservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soa.duo.ebayservice.model.OrganizationType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationInputDto {

    @NotBlank(message = "Organization name must not be empty")
    private String name;

    @Min(value = 1, message = "employeesCount must be >= 1")
    private Long employeesCount; // nullable, поэтому без @NotNull

    private OrganizationType type;
}