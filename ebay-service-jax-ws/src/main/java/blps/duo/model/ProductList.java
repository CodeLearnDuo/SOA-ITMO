package blps.duo.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ProductList") // <--- Должно быть обязательно!
@XmlType(name = "ProductList", namespace = "http://www.example.com/ebay")
public class ProductList {
    private List<Product> product;

    public ProductList() {
    }

    public ProductList(List<Product> product) {
        this.product = product;
    }

    @XmlElement(name = "product") // <--- JAXB теперь сможет правильно сериализовать список!
    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}