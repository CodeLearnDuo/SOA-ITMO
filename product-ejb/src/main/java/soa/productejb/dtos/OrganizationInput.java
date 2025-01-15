package soa.productejb.dtos;

import lombok.Data;
import soa.productejb.enums.OrganizationType;

@Data
public class OrganizationInput {

    private String name;
    private int employeesCount;
    private OrganizationType type;

    public OrganizationInput() {
    }

    public OrganizationInput(String name, int employeesCount, OrganizationType type) {
        this.name = name;
        this.employeesCount = employeesCount;
        this.type = type;
    }

}