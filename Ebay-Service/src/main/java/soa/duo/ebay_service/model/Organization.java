package soa.duo.ebay_service.model;

import lombok.Data;
import soa.duo.ebay_service.model.enums.OrganizationType;

@Data
public class Organization {

    private int id;

    private String name;

    private int employeesCount;

    private OrganizationType organizationType;

}
