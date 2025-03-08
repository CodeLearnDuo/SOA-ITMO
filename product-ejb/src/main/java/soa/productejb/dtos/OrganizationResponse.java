package soa.productejb.dtos;


import lombok.Data;
import soa.productejb.enums.OrganizationType;

import java.io.Serializable;

@Data
public class OrganizationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer employeesCount;
    private OrganizationType type;

    public OrganizationResponse() {
    }

    public OrganizationResponse(Integer id, String name, Integer employeesCount, OrganizationType type) {
        this.id = id;
        this.name = name;
        this.employeesCount = employeesCount;
        this.type = type;
    }
}
