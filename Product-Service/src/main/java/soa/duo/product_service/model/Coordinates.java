package soa.duo.product_service.model;

import jakarta.validation.constraints.DecimalMax;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class Coordinates {

    private Integer x;

    @DecimalMax(value = "398", inclusive = true)
    private int y;

}
