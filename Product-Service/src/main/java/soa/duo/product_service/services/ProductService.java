package soa.duo.product_service.services;

import org.springframework.data.domain.Page;
import soa.duo.product_service.dtos.OrganizationResponse;
import soa.duo.product_service.dtos.ProductInput;
import soa.duo.product_service.dtos.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Page<ProductResponse> getProducts(Integer page, Integer size, List<String> sort, List<String> filters);

    ProductResponse addProduct(ProductInput productInput);

    Optional<ProductResponse> getProductById(Integer id);

    ProductResponse updateProduct(Integer id, ProductInput productInput);

    void deleteProduct(Integer id);

    Double calculateTotalPrice();

    List<OrganizationResponse> getUniqueManufacturers();

}
