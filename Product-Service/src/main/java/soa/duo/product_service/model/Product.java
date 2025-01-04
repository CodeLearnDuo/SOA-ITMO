package soa.duo.product_service.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import soa.duo.product_service.model.enums.UnitOfMeasure;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(1)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    @Embedded
    @NotNull
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Nullable
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than 0.")
    @Column(nullable = true)
    private Double price;

    @Column(unique = true, nullable = true)
    private String partNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Organization manufacturer;

}

