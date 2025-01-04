package soa.duo.product_service.model;

import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

@Data
public class Coordinates {

    private Integer x;

    @DecimalMax(value = "398", inclusive = true)
    private int y;

}
