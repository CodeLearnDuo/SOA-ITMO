package soa.productejb.dtos;


import lombok.Data;
import soa.productejb.enums.OrganizationType;

@Data
public class OrganizationResponse {

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
