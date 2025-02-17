package blps.duo.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "IncreasePricesResponse", namespace = "http://www.example.com/ebay")
@XmlType(name = "IncreasePricesResponse", namespace = "http://www.example.com/ebay")
public class IncreasePricesResponse {
    // Нет содержимого согласно WSDL

    public IncreasePricesResponse() {
    }
}