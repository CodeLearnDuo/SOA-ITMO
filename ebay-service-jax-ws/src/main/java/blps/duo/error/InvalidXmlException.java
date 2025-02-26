package blps.duo.error;

import java.time.Instant;

public class InvalidXmlException extends EbayApiException {
    public InvalidXmlException(String message) {
        super(new Error(400, message, Instant.now()));
    }
}