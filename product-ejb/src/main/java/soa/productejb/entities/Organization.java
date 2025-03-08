package soa.productejb.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import soa.productejb.enums.OrganizationType;

import java.io.Serializable;

@Data
@Entity
@Table(name = "organizations")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

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
