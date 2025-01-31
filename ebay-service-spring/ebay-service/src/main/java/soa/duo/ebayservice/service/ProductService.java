package soa.duo.ebayservice.service;

import org.springframework.stereotype.Service;
import soa.duo.ebayservice.model.Product;
import soa.duo.ebayservice.model.UnitOfMeasure;
import soa.duo.ebayservice.model.dto.CoordinatesInputDto;
import soa.duo.ebayservice.model.dto.OrganizationInputDto;
import soa.duo.ebayservice.model.dto.ProductInputDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductHttpClient productHttpClient;

    public ProductService() {
        this.productHttpClient = new ProductHttpClient();
    }

    /**
     * Фильтрация всех продуктов по unitOfMeasure
     */
    public List<Product> fetchAndFilterProductsByUnitOfMeasure(String unitOfMeasureStr) throws Exception {
        UnitOfMeasure uom;
        try {
            uom = UnitOfMeasure.valueOf(unitOfMeasureStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit of measure: " + unitOfMeasureStr);
        }

        List<Product> allProducts = productHttpClient.getAllProducts();

        return allProducts.stream().filter(p -> p.getUnitOfMeasure() != null && p.getUnitOfMeasure().name().equalsIgnoreCase(uom.name())).collect(Collectors.toList());
    }

    /**
     * Увеличиваем цены всех продуктов на заданный процент.
     */
    public void updateAllProductPrices(double percent) throws Exception {
        if (percent < 0) {
            throw new IllegalArgumentException("Invalid percentage value (cannot be negative)");
        }

        List<Product> products = productHttpClient.getAllProducts();
        if (products.isEmpty()) {
            // Можно кинуть кастомное исключение для 404
            throw new RuntimeException("Products not found");
        }

        List<String> failedUpdates = new ArrayList<>();
        for (Product product : products) {
            double oldPrice = product.getPrice() == null ? 0.0 : product.getPrice();
            double newPrice = oldPrice * (1 + percent / 100);

            CoordinatesInputDto coordinatesInputDto = new CoordinatesInputDto(product.getCoordinates().getX(), product.getCoordinates().getY());
            OrganizationInputDto organizationInputDto = new OrganizationInputDto(product.getManufacturer().getName(), product.getManufacturer().getEmployeesCount(), product.getManufacturer().getType());
            // Собираем DTO — "патч" для продукта
            ProductInputDto patchDto = new ProductInputDto(product.getName(), coordinatesInputDto, newPrice, product.getPartNumber(), product.getUnitOfMeasure(), organizationInputDto);

            boolean success = productHttpClient.patchProduct(product.getId(), patchDto);
            if (!success) {
                // если вернулся не 200 и не было 404/503 (выше)
                failedUpdates.add(product.getId().toString());
            }
        }

        if (!failedUpdates.isEmpty()) {
            throw new RuntimeException("Failed to update products with ids: " + failedUpdates);
        }
    }
}