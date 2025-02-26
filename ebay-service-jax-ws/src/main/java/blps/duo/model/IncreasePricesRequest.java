package blps.duo.model;

import blps.duo.adapter.DoubleAdapter;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "IncreasePricesRequest", namespace = "http://www.example.com/ebay")
@XmlType(name = "IncreasePricesRequest", namespace = "http://www.example.com/ebay")
public class IncreasePricesRequest {

    @XmlJavaTypeAdapter(DoubleAdapter.class)
    private Double increasePercent;

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