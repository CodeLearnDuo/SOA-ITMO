package blps.duo.error;


import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.WebFault;

@XmlRootElement(name = "EbayApiException", namespace = "http://www.example.com/ebay")
@WebFault(name = "ApiFault", targetNamespace = "http://www.example.com/ebay", faultBean = "blps.duo.error.Error")
@XmlSeeAlso(blps.duo.error.Error.class)
public class EbayApiException extends RuntimeException {

    private Error faultInfo;

    public EbayApiException() {
        super();
    }

    public EbayApiException(Error faultInfo) {
        super(faultInfo != null ? faultInfo.getMessage() : null);
        this.faultInfo = faultInfo;
    }

    public EbayApiException(String message, Error faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public Error getFaultInfo() {
        return faultInfo;
    }

}