package soa.duo.ebay_service.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import soa.duo.ebay_service.dtos.ErrorResponse;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        String message = "Invalid parameter value. Must be a valid number.";

        if (exception instanceof jakarta.ws.rs.BadRequestException) {
            message = "Invalid parameter value. Must be a valid number.";
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(message))
                .build();
    }
}
