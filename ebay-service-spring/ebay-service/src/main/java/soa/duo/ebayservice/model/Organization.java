package soa.duo.ebayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private Long id;
    private String name;
    private Long employeesCount;      // Nullable
    private OrganizationType type;    // Nullable

}