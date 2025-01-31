package soa.productejb.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import soa.productejb.enums.UnitOfMeasure;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @Column(nullable = false, name = "creation_date")
    private LocalDateTime creationDate;

    @Nullable
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than 0.")
    @Column(nullable = true)
    private Double price;

    @Column(unique = true, nullable = true, name = "part_number")
    private String partNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "unit_of_measure")
    private UnitOfMeasure unitOfMeasure;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Organization manufacturer;

    public Product() {
    }
}
