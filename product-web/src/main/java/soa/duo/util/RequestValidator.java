package soa.duo.util;

import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RequestValidator {

    private static final List<String> ALLOWED_PARAMS = List.of("page", "size", "sort", "filter");

    /**
     * Проверяет параметры запроса на наличие невалидных.
     *
     * @param queryParams параметры из UriInfo
     * @return Response если есть ошибки, иначе null
     */
    public static Response validateQueryParameters(Map<String, List<String>> queryParams) {
        List<String> invalidParams = queryParams.keySet().stream()
                .filter(param -> !ALLOWED_PARAMS.contains(param))
                .collect(Collectors.toList());

        if (!invalidParams.isEmpty()) {
            return buildValidationErrorResponse(
                    Response.Status.BAD_REQUEST,
                    "Invalid query parameter value",
                    invalidParams
            );
        }

        return null;
    }

    private static Response buildValidationErrorResponse(Response.Status status, String message, List<String> invalidParams) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("invalidParameters", invalidParams);
        body.put("time", getCurrentTime());
        return Response.status(status).entity(body).build();
    }

    private static String getCurrentTime() {
        return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
}
