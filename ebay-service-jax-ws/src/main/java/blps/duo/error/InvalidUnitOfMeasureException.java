package blps.duo.error;

import java.time.Instant;

public class InvalidUnitOfMeasureException extends EbayApiException {
    public InvalidUnitOfMeasureException(String message) {
        super(new Error(400, message, Instant.now()));
    }
}