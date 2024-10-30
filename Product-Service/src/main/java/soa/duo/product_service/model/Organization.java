package soa.duo.product_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import soa.duo.product_service.model.enums.OrganizationType;

@Entity
@Table(name = "organizations")
@Data
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(1)
    private int id;

    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    @Min(1)
    private int employeesCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType organizationType;

}
