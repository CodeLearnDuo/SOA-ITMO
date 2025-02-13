package soa.duo.ebayservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import soa.duo.ebayservice.model.Product;
import soa.duo.ebayservice.model.UnitOfMeasure;
import soa.duo.ebayservice.model.dto.CoordinatesInputDto;
import soa.duo.ebayservice.model.dto.OrganizationInputDto;
import soa.duo.ebayservice.model.dto.ProductInputDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductHttpClient productHttpClient;

    public ProductService() {
        this.productHttpClient = new ProductHttpClient();
    }

    /**
     * Фильтрация всех продуктов по unitOfMeasure.
     */
    public List<Product> fetchAndFilterProductsByUnitOfMeasure(String unitOfMeasureStr) throws Exception {
        LOGGER.info("fetchAndFilterProductsByUnitOfMeasure called with unitOfMeasureStr: {}", unitOfMeasureStr);
        UnitOfMeasure uom;
        try {
            uom = UnitOfMeasure.valueOf(unitOfMeasureStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid unit of measure: {}", unitOfMeasureStr);
            throw new IllegalArgumentException("Invalid unit of measure: " + unitOfMeasureStr);
        }

        LOGGER.debug("Attempting to retrieve all products...");
        List<Product> allProducts = productHttpClient.getAllProducts();
        LOGGER.debug("Retrieved {} products.", allProducts.size());

        List<Product> filtered = allProducts.stream()
                .filter(p -> p.getUnitOfMeasure() != null && p.getUnitOfMeasure().name().equalsIgnoreCase(uom.name()))
                .collect(Collectors.toList());

        LOGGER.info("Found {} products matching unitOfMeasure: {}", filtered.size(), uom);
        return filtered;
    }

    /**
     * Увеличиваем цены всех продуктов на заданный процент.
     */
    public void updateAllProductPrices(double percent) throws Exception {
        LOGGER.info("updateAllProductPrices called with percent: {}", percent);
        if (percent < 0) {
            LOGGER.error("Invalid percentage value (cannot be negative): {}", percent);
            throw new IllegalArgumentException("Invalid percentage value (cannot be negative)");
        }

        LOGGER.debug("Attempting to retrieve all products...");
        List<Product> products = productHttpClient.getAllProducts();
        LOGGER.debug("Retrieved {} products.", products.size());

        if (products.isEmpty()) {
            LOGGER.warn("No products found to update.");
            throw new RuntimeException("Products not found");
        }

        List<String> failedUpdates = new ArrayList<>();
        for (Product product : products) {
            Double oldPrice = product.getPrice();
            if (Objects.isNull(oldPrice)) {
                LOGGER.debug("Skipping product with id {} since price is null.", product.getId());
                continue;
            }

            double newPrice = oldPrice * (1 + percent / 100);
            LOGGER.debug("Product id {} old price: {} => new price: {}", product.getId(), oldPrice, newPrice);

            CoordinatesInputDto coordinatesInputDto = new CoordinatesInputDto(
                    product.getCoordinates().getX(),
                    product.getCoordinates().getY()
            );
            OrganizationInputDto organizationInputDto = new OrganizationInputDto(
                    product.getManufacturer().getName(),
                    product.getManufacturer().getEmployeesCount(),
                    product.getManufacturer().getType()
            );

            // Собираем DTO — "патч" для продукта
            ProductInputDto patchDto = new ProductInputDto(
                    product.getName(),
                    coordinatesInputDto,
                    newPrice,
                    product.getPartNumber(),
                    product.getUnitOfMeasure(),
                    organizationInputDto
            );

            boolean success = productHttpClient.patchProduct(product.getId(), patchDto);
            if (!success) {
                LOGGER.warn("Failed to update product with id {}", product.getId());
                failedUpdates.add(product.getId().toString());
            } else {
                LOGGER.debug("Successfully updated product with id {}", product.getId());
            }
        }

        if (!failedUpdates.isEmpty()) {
            String msg = "Failed to update products with ids: " + failedUpdates;
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        LOGGER.info("Successfully updated prices for all products.");
    }
}