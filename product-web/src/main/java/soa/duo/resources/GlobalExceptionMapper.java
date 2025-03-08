package soa.duo.resources;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import soa.duo.exception.TooManyRequestsException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик ВСЕХ неперехваченных исключений.
 */
@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        Throwable rootCause = getRootCause(exception);

        if (rootCause instanceof IllegalArgumentException) {
            return buildValidationErrorResponse(
                    Response.Status.BAD_REQUEST,
                    "Invalid query parameter value",
                    List.of(rootCause.getMessage())
            );
        } else if (rootCause instanceof NoSuchElementException) {
            return buildErrorResponse(Response.Status.NOT_FOUND, rootCause.getMessage());
        } else if (rootCause instanceof TooManyRequestsException) {
            return buildErrorResponse(Response.Status.TOO_MANY_REQUESTS, rootCause.getMessage());
        }
        else if (rootCause instanceof ConstraintViolationException) {
            List<String> errors = ((ConstraintViolationException) rootCause).getConstraintViolations()
                    .stream()
                    .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                    .collect(Collectors.toList());
            return buildValidationErrorResponse(
                    Response.Status.fromStatusCode(422),
                    "Validation failed for one or more fields",
                    errors
            );
        }

        log.error("Unhandled exception: ", exception);
        return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private Response buildValidationErrorResponse(Response.Status status, String message, List<String> invalidParams) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("invalidParameters", invalidParams);
        body.put("time", getCurrentTime());
        return Response.status(status).entity(body).build();
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

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

}