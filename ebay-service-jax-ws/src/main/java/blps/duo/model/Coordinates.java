package blps.duo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Coordinates") // Теперь JAXB сможет сериализовать объект
@XmlType(name = "Coordinates", namespace = "http://www.example.com/ebay")
public class Coordinates {
    private long x;
    private float y;

    public Coordinates() {
    }

    public Coordinates(long x, float y) {
        this.x = x;
        this.y = y;
    }

    @XmlElement(name = "x") // Теперь JAXB корректно обработает `x`
    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    @XmlElement(name = "y") // Теперь JAXB корректно обработает `y`
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}