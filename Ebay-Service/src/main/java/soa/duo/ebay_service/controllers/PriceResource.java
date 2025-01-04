package soa.duo.ebay_service.controllers;

import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import soa.duo.ebay_service.dtos.DefaultErrorResponse;
import soa.duo.ebay_service.dtos.ErrorResponse;
import soa.duo.ebay_service.exception.ServiceUnavailableException;
import soa.duo.ebay_service.services.PriceUpdateService;

import java.util.NoSuchElementException;

@Path("/price")
public class PriceResource {

    private final PriceUpdateService priceUpdateService = new PriceUpdateService();

    @PATCH
    @Path("/increase/{percent}")
    @Produces("application/json")
    public Response increasePrices(@PathParam("percent") double percent) {
        if (percent <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid percentage value. Must be greater than 0."))
                    .build();
        }

        try {
            priceUpdateService.updateAllProductPrices(percent);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Products not found"))
                    .build();
        } catch (ServiceUnavailableException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DefaultErrorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            "Internal server error"))
                    .build();
        }
    }

}
