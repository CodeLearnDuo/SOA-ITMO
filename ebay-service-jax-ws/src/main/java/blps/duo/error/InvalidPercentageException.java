package blps.duo.error;

import java.time.Instant;

public class InvalidPercentageException extends EbayApiException {
    public InvalidPercentageException(String message) {
        super(new Error(400, message, Instant.now()));
    }
}