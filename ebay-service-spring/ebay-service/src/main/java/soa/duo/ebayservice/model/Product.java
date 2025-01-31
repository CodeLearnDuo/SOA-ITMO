package soa.duo.ebayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Double price;               // nullable
    private String partNumber;          // nullable
    private UnitOfMeasure unitOfMeasure;
    private Organization manufacturer;

}