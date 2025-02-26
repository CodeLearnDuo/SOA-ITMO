package blps.duo.error;

import java.time.Instant;

public class InternalServerErrorException extends EbayApiException {
    public InternalServerErrorException(String message) {
        super(new Error(500, message, Instant.now()));
    }
}