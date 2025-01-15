package soa.duo.resources;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(jakarta.validation.ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed for one or more fields");
        body.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        List<Map<String, String>> errors = ex.getConstraintViolations().stream().map(cv -> {
            Map<String, String> e = new HashMap<>();
            e.put("field", cv.getPropertyPath().toString());
            e.put("error", cv.getMessage());
            return e;
        }).collect(Collectors.toList());

        body.put("errors", errors);
        return Response.status(422).entity(body).build();
    }
}
