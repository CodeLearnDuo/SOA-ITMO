package soa.productejb.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer x;

    @DecimalMax(value = "398", inclusive = true)
    private int y;

    public Coordinates() {
    }
}
