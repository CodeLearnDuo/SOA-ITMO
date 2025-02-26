package blps.duo.error;


import blps.duo.adapter.InstantAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.Instant;

@XmlRootElement(name = "Error", namespace = "http://www.example.com/ebay")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {

    @XmlElement(required = true)
    private int code;

    @XmlElement(required = true)
    private String message;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private Instant time;

    public Error() {
    }

    public Error(int code, String message, Instant time) {
        this.code = code;
        this.message = message;
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}