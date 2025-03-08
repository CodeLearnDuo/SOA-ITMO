package soa.duo.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import soa.productejb.enums.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Path("/enums")
@Produces(MediaType.APPLICATION_JSON)
public class EnumResource {

    @GET
    @Path("/organization-type")
    public Response getOrganizationTypes() {
        try {
            OrganizationType[] types = OrganizationType.values();
            return Response.ok(types).build();
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(body).build();
        }
    }

    @GET
    @Path("/unit-of-measure")
    public Response getUnitOfMeasures() {
        try {
            UnitOfMeasure[] measures = UnitOfMeasure.values();
            return Response.ok(measures).build();
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(body).build();
        }
    }
}
