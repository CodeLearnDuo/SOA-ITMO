package soa.productejb.service;


import jakarta.ejb.Remote;
import soa.productejb.dtos.OrganizationResponse;
import soa.productejb.dtos.ProductInput;
import soa.productejb.dtos.ProductResponse;
import soa.productejb.util.PageData;

import java.util.List;
import java.util.Optional;

@Remote
public interface ProductServiceRemote {

    PageData<ProductResponse> getProducts(Integer page,
                                          Integer size,
                                          List<String> sort,
                                          List<String> filters);

    ProductResponse addProduct(ProductInput productInput);

    ProductResponse getProductById(Integer id);

    ProductResponse updateProduct(Integer id, ProductInput productInput);

    void deleteProduct(Integer id);

    Double calculateTotalPrice();

    List<OrganizationResponse> getUniqueManufacturers();
}
