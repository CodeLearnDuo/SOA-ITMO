package blps.duo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ProductList", namespace = "http://www.example.com/ebay")
public class ProductList {
    private List<Product> products;

    @XmlElement(name = "product", namespace = "http://www.example.com/ebay")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}