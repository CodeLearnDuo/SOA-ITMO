package blps.duo.error;

import java.time.Instant;

public class ProductsNotFoundException extends EbayApiException {
    public ProductsNotFoundException(String message) {
        super(new Error(404, message, Instant.now()));
    }
}