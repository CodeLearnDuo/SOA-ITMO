package soa.duo.ebay_service.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import soa.duo.ebay_service.services.ProductFilterService;

@Path("/filter")
public class ProductFilterResource {

    private final ProductFilterService productFilterService = new ProductFilterService();

    @GET
    @Path("/unit-of-measure/{unitOfMeasure}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductsByUnitOfMeasure(@PathParam("unitOfMeasure") String unitOfMeasure) {
        try {
            String result = productFilterService.fetchAndFilterProductsByUnitOfMeasure(unitOfMeasure);
            return Response.ok(result, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Service Unavailable\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
