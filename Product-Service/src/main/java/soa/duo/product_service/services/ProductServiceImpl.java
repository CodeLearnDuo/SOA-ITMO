package soa.duo.product_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import soa.duo.product_service.dtos.OrganizationResponse;
import soa.duo.product_service.dtos.ProductInput;
import soa.duo.product_service.dtos.ProductResponse;
import soa.duo.product_service.model.Organization;
import soa.duo.product_service.model.Product;
import soa.duo.product_service.model.enums.UnitOfMeasure;
import soa.duo.product_service.repositories.OrganizationRepository;
import soa.duo.product_service.repositories.ProductRepository;
import soa.duo.product_service.util.ProductSpecificationBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public Page<ProductResponse> getProducts(Integer page, Integer size, List<String> sort, List<String> filters) {

        if (page == null || page < -1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sort) {
            Sort.Direction direction = Sort.Direction.ASC;
            String field = sortField;

            if (sortField.startsWith("-")) {
                direction = Sort.Direction.DESC;
                field = sortField.substring(1);
            }

            if (!ProductSpecificationBuilder.isValidSortField(field)) {
                throw new IllegalArgumentException("Invalid sort field: '" + field + "'");
            }

            String mappedField = ProductSpecificationBuilder.mapField(field);
            orders.add(new Sort.Order(direction, mappedField));
        }

        Pageable pageable;
        if (page == -1) {
            long totalItems = productRepository.count();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            pageable = PageRequest.of(totalPages - 1, size, Sort.by(orders));
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.by(orders));
        }

        Specification<Product> spec = ProductSpecificationBuilder.buildSpecificationFromFilters(filters);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
    public ProductResponse addProduct(ProductInput productInput) {

        Double price = productInput.price() == null ? 0.1 : productInput.price();

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        if (productInput.partNumber() != null && !productInput.partNumber().isEmpty() &&  productRepository.existsByPartNumber(productInput.partNumber())) {
            throw new DataIntegrityViolationException("Duplicate unique field");
        }

        if (productInput.coordinates().getY() < 0 | productInput.coordinates().getX() < 0) {
            throw new DataIntegrityViolationException("Coordinate cannot be negative");
        }

        if (productInput.coordinates().getY() > 398) {
            throw new DataIntegrityViolationException("Coordinate Y out of range, must be in range [0, 398]");
        }

        Product product = new Product();
        product.setName(productInput.name());
        product.setCoordinates(productInput.coordinates());
        product.setCreationDate(LocalDateTime.now());
        product.setPrice(productInput.price());
        product.setPartNumber(productInput.partNumber());
        product.setUnitOfMeasure(productInput.unitOfMeasure());

        Organization manufacturer = new Organization();
        manufacturer.setName(productInput.manufacturer().name());
        manufacturer.setEmployeesCount(productInput.manufacturer().employeesCount());
        manufacturer.setType(productInput.manufacturer().type());

        Organization savedManufacturer = organizationRepository.save(manufacturer);

        product.setManufacturer(savedManufacturer);

        Product savedProduct = productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }

    @Override
    public Optional<ProductResponse> getProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::mapToProductResponse);
    }

    @Override
    public ProductResponse updateProduct(Integer id, ProductInput productInput) {

        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            throw new NoSuchElementException("Product with specified ID not found");
        }

        Product product = productOpt.get();

        if (!productInput.partNumber().equals(product.getPartNumber())) {
            if (productRepository.existsByPartNumber(productInput.partNumber())) {
                throw new DataIntegrityViolationException("Duplicate unique field");
            }
        }

        product.setName(productInput.name());
        product.setCoordinates(productInput.coordinates());
        product.setPrice(productInput.price());
        product.setPartNumber(productInput.partNumber());
        product.setUnitOfMeasure(productInput.unitOfMeasure());

        Organization manufacturer = product.getManufacturer();
        if (manufacturer == null) {
            manufacturer = new Organization();
        }

        manufacturer.setName(productInput.manufacturer().name());
        manufacturer.setEmployeesCount(productInput.manufacturer().employeesCount());
        manufacturer.setType(productInput.manufacturer().type());

        Organization savedManufacturer = organizationRepository.save(manufacturer);
        product.setManufacturer(savedManufacturer);

        Product updatedProduct = productRepository.save(product);

        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Integer id) {

        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product with specified ID not found");
        }

        productRepository.deleteById(id);
    }

    @Override
    public Double calculateTotalPrice() {
        Double totalPrice = productRepository.calculateTotalPrice();
        if (totalPrice == null) {
            return 0.0;
        }

        if (totalPrice > Double.MAX_VALUE) {
            log.error("Total price exceeds a safe threshold. Current value: {}", totalPrice);
            throw new ArithmeticException("The total price of products exceeds the maximum safe value.");
        }

        return totalPrice;
    }

    @Override
    public List<OrganizationResponse> getUniqueManufacturers() {
        List<Organization> manufacturers = productRepository.findDistinctManufacturers();

        return manufacturers.stream()
                .map(this::mapToOrganizationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void increasePricesForAllProducts(double percent) {
        if (percent <= 0) {
            throw new IllegalArgumentException("The increase percentage must be greater than 0");
        }
        productRepository.increasePricesForAllProducts(percent);
        productRepository.flush();
        log.info("Increasing prices by {}%", percent);
    }

    @Override
    public List<ProductResponse> getProductsByUnitOfMeasure(String unitOfMeasure) {
        if (unitOfMeasure == null || unitOfMeasure.isEmpty()) {
            throw new IllegalArgumentException("Invalid unit of measure parameter");
        }
        List<Product> products = productRepository.findByUnitOfMeasure(UnitOfMeasure.valueOf(unitOfMeasure));
        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }






    private ProductResponse mapToProductResponse(Product product) {
        OrganizationResponse organizationResponse = null;
        if (product.getManufacturer() != null) {
            organizationResponse = new OrganizationResponse(
                    product.getManufacturer().getId(),
                    product.getManufacturer().getName(),
                    product.getManufacturer().getEmployeesCount(),
                    product.getManufacturer().getType()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCoordinates(),
                product.getCreationDate(),
                product.getPrice(),
                product.getPartNumber(),
                product.getUnitOfMeasure(),
                organizationResponse
        );
    }


    private OrganizationResponse mapToOrganizationResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getEmployeesCount(),
                organization.getType()
        );
    }

    private String generateRandomPartNumber() {
        Random random = new Random();
        StringBuilder partNumber = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            partNumber.append(random.nextInt(10));
        }

        return partNumber.toString();
    }

}
