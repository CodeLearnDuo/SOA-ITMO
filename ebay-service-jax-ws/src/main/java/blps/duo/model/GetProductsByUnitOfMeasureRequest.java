package blps.duo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GetProductsByUnitOfMeasureRequest", namespace = "http://www.example.com/ebay")
@XmlType(name = "GetProductsByUnitOfMeasureRequest", namespace = "http://www.example.com/ebay")
public class GetProductsByUnitOfMeasureRequest {
    private UnitOfMeasure unitOfMeasure;

    public GetProductsByUnitOfMeasureRequest() {
    }

    public GetProductsByUnitOfMeasureRequest(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @XmlElement(name = "unitOfMeasure") // Теперь JAXB корректно обработает `UnitOfMeasure`
    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}