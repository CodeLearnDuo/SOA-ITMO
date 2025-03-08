package soa.productejb.dtos;

import lombok.Data;
import soa.productejb.enums.OrganizationType;

import java.io.Serializable;

@Data
public class OrganizationInput implements Serializable {

    private static final long serialVersionUID = 1L;

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