package soa.duo.ebay_service.controllers;

import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import soa.duo.ebay_service.services.PriceUpdateService;

@Path("/price")
public class PriceResource {

    private final PriceUpdateService priceUpdateService = new PriceUpdateService();

    @PATCH
    @Path("/increase/{percent}")
    @Produces("application/json")
    public Response increasePrices(@PathParam("percent") double percent) throws Exception {
        if (percent <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Percentage must be greater than 0\"}")
                    .build();
        }

        Response response = priceUpdateService.updateProductPrices(percent);
        return response;
    }
}
