package soa.duo.ebayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soa.duo.ebayservice.model.Product;
import soa.duo.ebayservice.controller.advice.InternalServiceException;
import soa.duo.ebayservice.controller.advice.InvalidInputException;
import soa.duo.ebayservice.controller.advice.ResourceNotFoundException;
import soa.duo.ebayservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ebay")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /filter/unit-of-measure/{unitOfMeasure}
     *
     * @param unitOfMeasure - Unit of measure to filter products by.
     * @return 200 (OK) - Returns a list of products matching the specified unit of measure.
     * @throws InvalidInputException     - If the unitOfMeasure is invalid (400).
     * @throws ResourceNotFoundException - If no products are found (404).
     * @throws InternalServiceException  - For unexpected errors (500).
     */
    @GetMapping("/filter/unit-of-measure/{unitOfMeasure}")
    public ResponseEntity<List<Product>> getProductsByUnitOfMeasure(
            @PathVariable("unitOfMeasure") String unitOfMeasure) {
        try {
            List<Product> products = productService.fetchAndFilterProductsByUnitOfMeasure(unitOfMeasure);

            if (products.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No products found with the specified unit of measure"
                );
            }

            return ResponseEntity.ok(products); // 200 OK
        } catch (IllegalArgumentException ex) {
            throw new InvalidInputException("Invalid unit of measure: " + unitOfMeasure); // 400 Bad Request
        } catch (ResourceNotFoundException ex) {
            throw ex; // 404 Not Found
        } catch (Exception ex) {
            throw new InternalServiceException("An unexpected error occurred", ex); // 500 Internal Server Error
        }
    }

    /**
     * PATCH /price/increase/{increasePercent}
     *
     * @param increasePercent - Percentage to increase prices by.
     * @return 204 (No Content) - If the prices are updated successfully.
     * @throws InvalidInputException     - If the percentage is invalid (400).
     * @throws ResourceNotFoundException - If no products are found (404).
     * @throws InternalServiceException  - For unexpected errors (500).
     */
    @PatchMapping("/price/increase/{increasePercent}")
    public ResponseEntity<Void> increasePrice(@PathVariable("increasePercent") Double increasePercent) {
        try {
            productService.updateAllProductPrices(increasePercent);

            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException ex) {
            throw new InvalidInputException("Invalid percentage value: " + increasePercent); // 400 Bad Request
        } catch (ResourceNotFoundException ex) {
            throw ex; // 404 Not Found
        } catch (Exception ex) {
            throw new InternalServiceException("An unexpected error occurred", ex); // 500 Internal Server Error
        }
    }
}