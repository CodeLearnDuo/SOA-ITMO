package soa.duo.ebayservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soa.duo.ebayservice.model.UnitOfMeasure;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInputDto {

    @NotBlank(message = "Product name must not be empty")
    private String name;

    @NotNull(message = "Coordinates must not be null")
    @Valid
    private CoordinatesInputDto coordinates;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price; // nullable

    private String partNumber; // nullable

    @NotNull(message = "unitOfMeasure must not be null")
    private UnitOfMeasure unitOfMeasure;

    @NotNull(message = "manufacturer must not be null")
    @Valid
    private OrganizationInputDto manufacturer;

}