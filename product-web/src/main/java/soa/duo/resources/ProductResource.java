package soa.duo.resources;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import soa.productejb.dtos.*;
import soa.productejb.service.*;
import soa.productejb.util.*;

import java.util.*;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

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
                                @QueryParam("filter") List<String> filters) {
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

        Optional<ProductResponse> opt = productService.getProductById(id);
        if (opt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Product with specified ID not found"))
                    .build();
        }
        return Response.ok(opt.get()).build();
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

        // Если в EJB нет, кидаем NoSuchElementException -> ловим в маппере
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
}
