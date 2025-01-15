package soa.productejb.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

@Data
@Embeddable
public class Coordinates {

    private Integer x;

    @DecimalMax(value = "398", inclusive = true)
    private int y;

    public Coordinates() {
    }
}
