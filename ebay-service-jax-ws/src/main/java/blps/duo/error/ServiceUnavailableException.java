package blps.duo.error;

import java.time.Instant;

public class ServiceUnavailableException extends EbayApiException {
    public ServiceUnavailableException(String message) {
        super(new Error(503, message, Instant.now()));
    }
}