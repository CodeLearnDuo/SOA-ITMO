package blps.duo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GetProductsByUnitOfMeasureResponse", namespace = "http://www.example.com/ebay")
@XmlType(name = "GetProductsByUnitOfMeasureResponse", namespace = "http://www.example.com/ebay")
public class GetProductsByUnitOfMeasureResponse {
    private ProductList products;

    public GetProductsByUnitOfMeasureResponse() {
    }

    public GetProductsByUnitOfMeasureResponse(ProductList products) {
        this.products = products;
    }

    @XmlElement(name = "products") // Теперь JAXB корректно обработает `ProductList`
    public ProductList getProducts() {
        return products;
    }

    public void setProducts(ProductList products) {
        this.products = products;
    }
}