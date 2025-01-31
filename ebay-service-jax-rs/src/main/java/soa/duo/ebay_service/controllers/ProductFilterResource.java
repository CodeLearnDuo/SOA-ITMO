package soa.duo.ebay_service.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import soa.duo.ebay_service.dtos.DefaultErrorResponse;
import soa.duo.ebay_service.dtos.ErrorResponse;
import soa.duo.ebay_service.exception.ServiceUnavailableException;
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
            if (result.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No products found with the specified unit of measure"))
                        .build();
            }
            return Response.ok(result, MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid unit of measure parameter"))
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
