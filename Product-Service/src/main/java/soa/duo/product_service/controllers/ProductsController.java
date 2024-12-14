package soa.duo.product_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import soa.duo.product_service.dtos.OrganizationResponse;
import soa.duo.product_service.dtos.ProductInput;
import soa.duo.product_service.dtos.ProductResponse;
import soa.duo.product_service.services.ProductService;


import java.util.*;

@Slf4j
@RestController
@RequestMapping("/products")
@Validated
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") List<String> sort,
            @RequestParam(value = "filter", required = false) List<String> filters) {

        int maxAllowedPage = 1_000_000;
        int maxAllowedSize = 10_000;

        if (page < 1) {
            throw new IllegalArgumentException("Page number must be a positive integer.");
        }

        if (page > maxAllowedPage) {
            throw new IllegalArgumentException("Page number is too large.");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must be a positive integer.");
        }

        if (size > maxAllowedSize) {
            throw new IllegalArgumentException("Page size is too large.");
        }

        Page<ProductResponse> products = productService.getProducts(page, size, sort, filters);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/")
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductInput productInput,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("Validation failed for one or more fields");
        }

        ProductResponse productResponse = productService.addProduct(productInput);
        log.info("Product added successfully {}", productResponse.toString());
        return ResponseEntity.status(201).body(productResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer id) {
        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        Optional<ProductResponse> productOpt = productService.getProductById(id);
        return ResponseEntity.ok(productOpt.orElseThrow(() -> new NoSuchElementException("Product with specified ID not found")));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ProductInput productInput,
            BindingResult bindingResult) {

        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("Validation failed for one or more fields");
        }

        ProductResponse productResponse = productService.updateProduct(id, productInput);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        if (id < 1) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/price/sum")
    public ResponseEntity<Double> calculateTotalPrice() {
        Double totalPrice = productService.calculateTotalPrice();
        return ResponseEntity.ok(totalPrice);
    }

    @GetMapping("/manufacturers")
    public ResponseEntity<List<OrganizationResponse>> getUniqueManufacturers() {
        List<OrganizationResponse> manufacturers = productService.getUniqueManufacturers();
        return ResponseEntity.ok(manufacturers);
    }
}
