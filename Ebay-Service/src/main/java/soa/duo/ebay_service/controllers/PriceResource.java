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
    public Response increasePrices(@PathParam("percent") double percent) {
        if (percent <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{message: Percentage must be greater than 0}")
                    .build();
        }
        try {
            String updateResult = priceUpdateService.updateAllProductPrices(percent);
            return Response.ok("{message: Prices updated successfully, details: " + updateResult + "}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{message: Failed to update product prices}")
                    .build();
        }
    }
}
