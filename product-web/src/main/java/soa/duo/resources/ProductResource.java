package soa.duo.resources;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import soa.duo.util.RequestValidator;
import soa.productejb.dtos.*;
import soa.productejb.service.*;
import soa.productejb.util.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private static final int REQUEST_LIMIT = 100;
    private static final long TIME_WINDOW_MS = 60 * 1000;
    private static final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    @EJB
    private ProductServiceRemote productService;

    /**
     * GET /products/?page=?&size=?&sort=?&filter=?
     * Аналог вашего Spring метода getProducts.
     */
    @GET
    public Response getProducts(@QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("size") @DefaultValue("10") int size,
                                @QueryParam("sort") List<String> sort,
                                @QueryParam("filter") List<String> filters,
                                @Context UriInfo uriInfo) {

        String clientIp = uriInfo.getRequestUri().getHost();
        long currentTime = System.currentTimeMillis();

        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        requestTimestamps.putIfAbsent(clientIp, currentTime);

        long lastRequestTime = requestTimestamps.get(clientIp);
        if (currentTime - lastRequestTime > TIME_WINDOW_MS) {
            requestCounts.get(clientIp).set(0);
            requestTimestamps.put(clientIp, currentTime);
        }

        if (requestCounts.get(clientIp).incrementAndGet() > REQUEST_LIMIT) {
            return Response.status(429)
                    .entity(Map.of(
                            "message", "Too Many Requests",
                            "retryAfter", TIME_WINDOW_MS / 1000 + " seconds"
                    ))
                    .build();
        }

        Response validationResponse = RequestValidator.validateQueryParameters(uriInfo.getQueryParameters());
        if (validationResponse != null) {
            return validationResponse;
        }

        PageData<ProductResponse> pageData = productService.getProducts(page, size, sort, filters);
        return Response.ok(pageData).build();

    }

    /**
     * POST /products/
     * Создаёт новый продукт.
     */
    @POST
    public Response addProduct(ProductInput productInput) {
        ProductResponse created = productService.addProduct(productInput);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * GET /products/{id}
     * Получить продукт по ID.
     */
    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Integer id) {
        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        ProductResponse product = productService.getProductById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Product with specified ID not found"))
                    .build();
        }
        return Response.ok(product).build();
    }

    /**
     * PATCH /products/{id}
     * Обновляем продукт (аналог вашего PATCH).
     */
    @PATCH
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Integer id,
                                  ProductInput productInput) {
        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        ProductResponse updated = productService.updateProduct(id, productInput);
        return Response.ok(updated).build();
    }

    /**
     * DELETE /products/{id}
     * Удаляем продукт.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Integer id) {
        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        productService.deleteProduct(id);
        return Response.noContent().build();
    }

    /**
     * GET /products/price/sum
     * Возвращает сумму цен всех продуктов.
     */
    @GET
    @Path("/price/sum")
    public Response calculateTotalPrice() {
        Double totalPrice = productService.calculateTotalPrice();
        Map<String, Object> body = new HashMap<>();
        body.put("totalPrice", totalPrice);
        return Response.ok(body).build();
    }

    /**
     * GET /products/manufacturers
     * Возвращает список уникальных производителей.
     */
    @GET
    @Path("/manufacturers")
    public Response getUniqueManufacturers() {
        List<OrganizationResponse> manufacturers = productService.getUniqueManufacturers();
        return Response.ok(manufacturers).build();
    }

    /**
     * GET /products/health
     * Проверяет работоспособность сервиса.
     */
    @GET
    @Path("/health")
    public Response healthCheck(@QueryParam("status") String status) {
        if ("check".equals(status)) {
            return Response.ok(Map.of("status", "Service is healthy")).build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("message", "Invalid query parameter value"))
                .build();
    }
}
