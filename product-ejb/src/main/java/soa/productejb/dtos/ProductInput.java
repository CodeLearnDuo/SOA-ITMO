package soa.productejb.dtos;

import lombok.Data;
import soa.productejb.entities.Coordinates;
import soa.productejb.enums.UnitOfMeasure;

@Data
public class ProductInput {

    private String name;
    private Coordinates coordinates;
    private Double price;
    private String partNumber;
    private UnitOfMeasure unitOfMeasure;
    private OrganizationInput manufacturer;

    public ProductInput() {
    }

    public ProductInput(String name,
                        Coordinates coordinates,
                        Double price,
                        String partNumber,
                        UnitOfMeasure unitOfMeasure,
                        OrganizationInput manufacturer) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.partNumber = partNumber;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
    }

}
