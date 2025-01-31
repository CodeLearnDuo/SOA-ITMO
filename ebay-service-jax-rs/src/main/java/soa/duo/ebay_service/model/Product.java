package soa.duo.ebay_service.model;

import lombok.Data;
import soa.duo.ebay_service.model.enums.UnitOfMeasure;

import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Double price;
    private String partNumber;
    private UnitOfMeasure unitOfMeasure;
    private Organization manufacturer;
}
