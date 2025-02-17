
package blps.duo.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "IncreasePricesRequest", namespace = "http://www.example.com/ebay")
@XmlType(name = "IncreasePricesRequest", namespace = "http://www.example.com/ebay")
public class IncreasePricesRequest {
    private double increasePercent;

    public IncreasePricesRequest() {
    }

    public IncreasePricesRequest(double increasePercent) {
        this.increasePercent = increasePercent;
    }

    public double getIncreasePercent() {
        return increasePercent;
    }

    public void setIncreasePercent(double increasePercent) {
        this.increasePercent = increasePercent;
    }
}