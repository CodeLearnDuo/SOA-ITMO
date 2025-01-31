package soa.productejb.dtos;

import lombok.Data;
import soa.productejb.entities.Coordinates;
import soa.productejb.enums.UnitOfMeasure;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProductResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Double price;
    private String partNumber;
    private UnitOfMeasure unitOfMeasure;
    private OrganizationResponse manufacturer;

    public ProductResponse() {
    }

    public ProductResponse(Integer id,
                           String name,
                           Coordinates coordinates,
                           LocalDateTime creationDate,
                           Double price,
                           String partNumber,
                           UnitOfMeasure unitOfMeasure,
                           OrganizationResponse manufacturer) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.partNumber = partNumber;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
    }
}
