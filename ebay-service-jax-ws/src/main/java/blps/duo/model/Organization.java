package blps.duo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Organization") // Теперь JAXB сможет сериализовать объект
@XmlType(name = "Organization", namespace = "http://www.example.com/ebay")
public class Organization {
    private long id;
    private String name;
    private Long employeesCount;
    private OrganizationType type;

    public Organization() {
    }

    public Organization(long id, String name, Long employeesCount, OrganizationType type) {
        this.id = id;
        this.name = name;
        this.employeesCount = employeesCount;
        this.type = type;
    }

    @XmlElement
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(nillable = true) // Разрешаем null, так как в WSDL поле опциональное
    public Long getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(Long employeesCount) {
        this.employeesCount = employeesCount;
    }

    @XmlElement
    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }
}