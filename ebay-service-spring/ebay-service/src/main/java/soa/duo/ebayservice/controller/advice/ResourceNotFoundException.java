package soa.duo.ebayservice.controller.advice;

/**
 * Исключение для 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
