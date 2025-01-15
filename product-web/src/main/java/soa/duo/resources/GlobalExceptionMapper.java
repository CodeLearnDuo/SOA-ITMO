package soa.duo.resources;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик ВСЕХ неперехваченных исключений.
 */
@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof IllegalArgumentException) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        } else if (exception instanceof java.util.NoSuchElementException) {
            return buildErrorResponse(Response.Status.NOT_FOUND, exception.getMessage());
        } else if (exception instanceof ConstraintViolationException
                || exception.getMessage().contains("Duplicate unique field")) {
            return buildErrorResponse(Response.Status.CONFLICT, "Duplicate unique field or constraint violation");
        }

        log.error("Unhandled exception: ", exception);

        return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("time", getCurrentTime());
        return Response.status(status).entity(body).build();
    }

    private String getCurrentTime() {
        return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

}