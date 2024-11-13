package soa.duo.product_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import soa.duo.product_service.dtos.OrganizationResponse;
import soa.duo.product_service.dtos.ProductInput;
import soa.duo.product_service.dtos.ProductResponse;
import soa.duo.product_service.services.ProductService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Validated
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") List<String> sort,
            @RequestParam(value = "filter", required = false) List<String> filters) {

        try {
            Page<ProductResponse> products = productService.getProducts(page, size, sort, filters);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            body.put("time", LocalDateTime.now());
            log.error(body.toString());
            return ResponseEntity.badRequest().body(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductInput productInput, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Validation failed for one or more fields");
            body.put("time", LocalDateTime.now());
            body.put("errors", bindingResult.getFieldErrors().stream().map(error -> {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("field", error.getField());
                errorMap.put("error", error.getDefaultMessage());
                return errorMap;
            }));
            return ResponseEntity.unprocessableEntity().body(body);
        }

        try {
            ProductResponse productResponse = productService.addProduct(productInput);
            log.info("Product added successfully {}", productResponse.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Duplicate unique field");
            body.put("time", LocalDateTime.now());
            log.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid data in request");
            body.put("time", LocalDateTime.now());

            log.error("Internal server error", e);
            return ResponseEntity.badRequest().body(body);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Integer id) {
        if (id < 1) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid product ID");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.badRequest().body(body);
        }

        try {
            Optional<ProductResponse> productOpt = productService.getProductById(id);
            if (productOpt.isPresent()) {
                return ResponseEntity.ok(productOpt.get());
            } else {
                Map<String, Object> body = new HashMap<>();
                body.put("message", "Product with specified ID not found");
                body.put("time", LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ProductInput productInput,
            BindingResult bindingResult) {
        if (id < 1) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid product ID");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.badRequest().body(body);
        }

        if (bindingResult.hasErrors()) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Validation failed for one or more fields");
            body.put("time", LocalDateTime.now());
            body.put("errors", bindingResult.getFieldErrors().stream().map(error -> {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("field", error.getField());
                errorMap.put("error", error.getDefaultMessage());
                return errorMap;
            }));
            return ResponseEntity.unprocessableEntity().body(body);
        }

        try {
            ProductResponse productResponse = productService.updateProduct(id, productInput);
            return ResponseEntity.ok(productResponse);
        } catch (NoSuchElementException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Duplicate unique field");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid data in request");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.badRequest().body(body);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id) {
        if (id < 1) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid product ID");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.badRequest().body(body);
        }

        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @GetMapping("/price/sum")
    public ResponseEntity<?> calculateTotalPrice() {
        try {
            Double totalPrice = productService.calculateTotalPrice();
            Map<String, Object> body = new HashMap<>();
            body.put("totalPrice", totalPrice);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @GetMapping("/manufacturers")
    public ResponseEntity<?> getUniqueManufacturers() {
        try {
            List<OrganizationResponse> manufacturers = productService.getUniqueManufacturers();
            return ResponseEntity.ok(manufacturers);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

//    @PatchMapping("/price/increase/{percent}")
//    public ResponseEntity<?> increasePrices(@PathVariable("percent") double percent) {
//        try {
//            productService.increasePricesForAllProducts(percent);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            Map<String, Object> body = new HashMap<>();
//            body.put("message", e.getMessage());
//            body.put("time", LocalDateTime.now());
//            return ResponseEntity.badRequest().body(body);
//        } catch (Exception e) {
//            Map<String, Object> body = new HashMap<>();
//            body.put("message", "Internal server error");
//            body.put("time", LocalDateTime.now());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
//        }
//    }
//
//    @GetMapping("/filter/unit-of-measure/{unitOfMeasure}")
//    public ResponseEntity<?> getProductsByUnitOfMeasure(@PathVariable("unitOfMeasure") String unitOfMeasure) {
//        try {
//            if (Arrays.stream(UnitOfMeasure.values()).noneMatch(u -> u.name().equalsIgnoreCase(unitOfMeasure))) {
//                Map<String, Object> body = new HashMap<>();
//                body.put("message", "Invalid unit of measure parameter");
//                body.put("time", LocalDateTime.now());
//                return ResponseEntity.badRequest().body(body);
//            }
//
//            List<ProductResponse> products = productService.getProductsByUnitOfMeasure(unitOfMeasure.toUpperCase());
//
//            if (products.isEmpty()) {
//                Map<String, Object> body = new HashMap<>();
//                body.put("message", "No products found with the specified unit of measure");
//                body.put("time", LocalDateTime.now());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
//            }
//
//            return ResponseEntity.ok(products);
//
//        } catch (Exception e) {
//            log.debug(e.getMessage());
//            Map<String, Object> body = new HashMap<>();
//            body.put("message", "Internal server error");
//            body.put("time", LocalDateTime.now());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
//        }
//    }

}
