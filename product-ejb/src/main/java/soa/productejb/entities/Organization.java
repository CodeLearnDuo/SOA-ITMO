package soa.productejb.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import soa.productejb.enums.OrganizationType;

@Data
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(1)
    private Integer id;

    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    @Min(1)
    @Column(name = "employees_count")
    private int employeesCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType type;

    public Organization() {
    }

}
