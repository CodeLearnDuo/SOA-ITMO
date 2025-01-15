package soa.productejb.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.criteria.*;
import soa.productejb.dtos.OrganizationResponse;
import soa.productejb.dtos.ProductInput;
import soa.productejb.dtos.ProductResponse;
import soa.productejb.entities.Organization;
import soa.productejb.entities.Product;
import soa.productejb.enums.UnitOfMeasure;
import soa.productejb.util.PageData;
import soa.productejb.util.ProductSpecificationBuilder;
import soa.productejb.util.Specification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
public class ProductServiceBean implements ProductServiceRemote {

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    /**
     * Аналог вашего Spring-кода getProducts(page, size, sort, filters).
     * Здесь вместо Spring Data Page используем собственный PageData.
     */
    @Override
    public PageData<ProductResponse> getProducts(Integer page,
                                                 Integer size,
                                                 List<String> sort,
                                                 List<String> filters) {
        // Проверки входных параметров, как было в Spring:
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be a positive integer.");
        }
        if (page > 1_000_000) {
            throw new IllegalArgumentException("Page number is too large.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be a positive integer.");
        }
        if (size > 10_000) {
            throw new IllegalArgumentException("Page size is too large.");
        }

        // 1) Собираем CriteriaQuery на основе Specification (фильтров)
        Specification<Product> spec = ProductSpecificationBuilder.buildSpecificationFromFilters(filters);

        // --- Подготовка Criteria для основной выборки ---
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        // Фильтрация (Predicate) через наш spec
        Predicate specPredicate = (spec != null)
                ? spec.toPredicate(root, cq, cb)
                : cb.conjunction();
        cq.where(specPredicate);

        // Сортировка (order by)
        List<Order> orderList = buildOrderList(sort, cb, root);
        if (!orderList.isEmpty()) {
            cq.orderBy(orderList);
        }

        // Создаём TypedQuery
        TypedQuery<Product> mainQuery = em.createQuery(cq);

        // 2) Считаем общее количество (через отдельный CriteriaQuery<Long>)
        long totalElements = countProductsWithSpec(spec);

        // Если page == -1 => переходим на "последнюю" страницу
        int pageIndex;
        if (page == -1) {
            int totalPages = (int) Math.ceil((double) totalElements / size);
            pageIndex = Math.max(0, totalPages - 1);
        } else {
            pageIndex = page - 1;
        }

        // Пагинация
        mainQuery.setFirstResult(pageIndex * size);
        mainQuery.setMaxResults(size);

        List<Product> products = mainQuery.getResultList();
        List<ProductResponse> content = products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageData<>(content, pageIndex + 1, size, totalElements);
    }

    /**
     * Аналог вашего Spring addProduct(productInput)
     */
    @Override
    public ProductResponse addProduct(ProductInput productInput) {
        if (productInput.getPrice() != null && productInput.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0 if specified.");
        }

        // partNumber: проверка уникальности
        String partNumber = productInput.getPartNumber();
        if (partNumber != null && partNumber.trim().isEmpty()) {
            partNumber = null;
        }
        if (partNumber != null && existsByPartNumber(partNumber)) {
            throw new IllegalArgumentException("Duplicate unique field");
        }

        // Координаты не должны быть отрицательными
        if (productInput.getCoordinates().getX() < 0 || productInput.getCoordinates().getY() < 0) {
            throw new IllegalArgumentException("Coordinate cannot be negative");
        }
        if (productInput.getCoordinates().getY() > 398) {
            throw new IllegalArgumentException("Coordinate Y out of range, must be in range [0, 398]");
        }

        Product product = new Product();
        product.setName(productInput.getName());
        product.setCoordinates(productInput.getCoordinates());
        product.setCreationDate(LocalDateTime.now());
        product.setPrice(productInput.getPrice());
        product.setPartNumber(partNumber);
        product.setUnitOfMeasure(productInput.getUnitOfMeasure());

        // Создаём/сохраняем Organization
        Organization organization = new Organization();
        organization.setName(productInput.getManufacturer().getName());
        organization.setEmployeesCount(productInput.getManufacturer().getEmployeesCount());
        organization.setType(productInput.getManufacturer().getType());
        em.persist(organization);

        product.setManufacturer(organization);
        em.persist(product);

        return mapToProductResponse(product);
    }

    /**
     * Аналог Spring getProductById
     */
    @Override
    public Optional<ProductResponse> getProductById(Integer id) {
        Product product = em.find(Product.class, id);
        if (product == null) {
            return Optional.empty();
        }
        return Optional.of(mapToProductResponse(product));
    }

    /**
     * Аналог Spring updateProduct
     */
    @Override
    public ProductResponse updateProduct(Integer id, ProductInput productInput) {
        Product product = em.find(Product.class, id);
        if (product == null) {
            throw new NoSuchElementException("Product with specified ID not found");
        }

        String partNumber = productInput.getPartNumber();
        if (partNumber != null && partNumber.trim().isEmpty()) {
            partNumber = null;
        }
        if (partNumber != null && !partNumber.equals(product.getPartNumber())) {
            // проверяем уникальность
            if (existsByPartNumber(partNumber)) {
                throw new IllegalArgumentException("Duplicate unique field");
            }
        }

        product.setName(productInput.getName());
        product.setCoordinates(productInput.getCoordinates());
        product.setPrice(productInput.getPrice());
        product.setPartNumber(partNumber);
        product.setUnitOfMeasure(productInput.getUnitOfMeasure());

        Organization manufacturer = product.getManufacturer();
        if (manufacturer == null) {
            manufacturer = new Organization();
        }
        manufacturer.setName(productInput.getManufacturer().getName());
        manufacturer.setEmployeesCount(productInput.getManufacturer().getEmployeesCount());
        manufacturer.setType(productInput.getManufacturer().getType());
        em.merge(manufacturer);

        product.setManufacturer(manufacturer);
        em.merge(product);

        return mapToProductResponse(product);
    }

    /**
     * Аналог Spring deleteProduct
     */
    @Override
    public void deleteProduct(Integer id) {
        Product product = em.find(Product.class, id);
        if (product == null) {
            throw new NoSuchElementException("Product with specified ID not found");
        }
        em.remove(product);
    }

    /**
     * Аналог Spring calculateTotalPrice
     */
    @Override
    public Double calculateTotalPrice() {
        TypedQuery<Double> query = em.createQuery("SELECT SUM(p.price) FROM Product p", Double.class);
        Double totalPrice = query.getSingleResult();
        if (totalPrice == null) {
            return 0.0;
        }
        if (totalPrice > Double.MAX_VALUE) {
            log.error("Total price exceeds a safe threshold. Current value: {}", totalPrice);
            throw new ArithmeticException("The total price of products exceeds the maximum safe value.");
        }
        return totalPrice;
    }

    /**
     * Аналог Spring getUniqueManufacturers
     */
    @Override
    public List<OrganizationResponse> getUniqueManufacturers() {
        TypedQuery<Organization> query = em.createQuery(
                "SELECT DISTINCT p.manufacturer FROM Product p WHERE p.manufacturer IS NOT NULL",
                Organization.class
        );
        List<Organization> orgs = query.getResultList();

        return orgs.stream()
                .map(this::mapToOrganizationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Аналог Spring increasePricesForAllProducts
     */
    @Override
    @Transactional
    public void increasePricesForAllProducts(double percent) {
        if (percent <= 0) {
            throw new IllegalArgumentException("The increase percentage must be greater than 0");
        }
        em.createQuery("UPDATE Product p SET p.price = p.price * (1 + :percent / 100.0)")
                .setParameter("percent", percent)
                .executeUpdate();
        em.flush();
        log.info("Increasing prices by {}%", percent);
    }

    /**
     * Аналог Spring getProductsByUnitOfMeasure
     */
    @Override
    public List<ProductResponse> getProductsByUnitOfMeasure(String unitOfMeasure) {
        if (unitOfMeasure == null || unitOfMeasure.isEmpty()) {
            throw new IllegalArgumentException("Invalid unit of measure parameter");
        }
        UnitOfMeasure uom = UnitOfMeasure.valueOf(unitOfMeasure.toUpperCase());
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.unitOfMeasure = :uom", Product.class);
        query.setParameter("uom", uom);
        List<Product> products = query.getResultList();
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // ========== ВСПОМОГАТЕЛЬНЫЕ / ПРИВАТНЫЕ МЕТОДЫ ==========
    // =========================================================================

    /**
     * Собирает список Order (ASC/DESC) на основе переданного списка полей.
     * Аналогично тому, как в Spring-е вы делали sortField.startsWith("-") и т.д.
     */
    private List<Order> buildOrderList(List<String> sortFields, CriteriaBuilder cb, Root<Product> root) {
        // Если список пуст или null -> сортируем по "id ASC" (как в вашем Spring default = "id")
        if (sortFields == null || sortFields.isEmpty()) {
            return Collections.singletonList(cb.asc(root.get("id")));
        }

        List<Order> orders = new ArrayList<>();
        for (String sortField : sortFields) {
            boolean isDesc = false;
            String field = sortField;
            if (sortField.startsWith("-")) {
                isDesc = true;
                field = sortField.substring(1);
            }
            // Проверяем, что поле допустимо
            if (!ProductSpecificationBuilder.isValidSortField(field)) {
                throw new IllegalArgumentException("Invalid sort field: '" + field + "'");
            }
            String mappedField = ProductSpecificationBuilder.mapField(field);

            // Получаем Path (учитывая вложенные поля)
            Path<?> path = getPathForSorting(root, mappedField);
            // Добавляем Order
            if (isDesc) {
                orders.add(cb.desc(path));
            } else {
                orders.add(cb.asc(path));
            }
        }

        // Если после цикла не было полей -> fallback "id ASC"
        if (orders.isEmpty()) {
            orders.add(cb.asc(root.get("id")));
        }
        return orders;
    }

    /**
     * Возвращает Path<?> к нужному полю, например:
     *  - "manufacturer.id" => root.get("manufacturer").get("id")
     *  - "coordinates.x"   => root.get("coordinates").get("x")
     *  - "id"              => root.get("id")
     */
    private Path<?> getPathForSorting(Root<Product> root, String mappedField) {
        String[] parts = mappedField.split("\\.");
        From<?, ?> from = root;
        Path<?> path = root;

        for (String part : parts) {
            if ("manufacturer".equals(part)) {
                from = from.join("manufacturer", JoinType.LEFT);
                path = from;
            } else if ("coordinates".equals(part)) {
                path = path.get(part);
            } else {
                path = path.get(part);
            }
        }
        return path;
    }

    /**
     * Считаем общее количество (totalElements) с учётом Specification.
     * Аналогично тому, как Spring Data делал .count(spec).
     */
    private long countProductsWithSpec(Specification<Product> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
        Root<Product> rootCount = cqCount.from(Product.class);
        cqCount.select(cb.count(rootCount));

        Predicate predicate = (spec != null)
                ? spec.toPredicate(rootCount, cqCount, cb)
                : cb.conjunction();
        cqCount.where(predicate);

        TypedQuery<Long> typedQueryCount = em.createQuery(cqCount);
        Long total = typedQueryCount.getSingleResult();
        return (total != null) ? total : 0L;
    }

    /**
     * Проверяем, существует ли уже продукт с таким partNumber (для уникальности).
     */
    private boolean existsByPartNumber(String partNumber) {
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.partNumber = :pn", Long.class);
        q.setParameter("pn", partNumber);
        Long count = q.getSingleResult();
        return (count != null && count > 0);
    }

    /**
     * Конвертируем Product -> ProductResponse
     */
    private ProductResponse mapToProductResponse(Product product) {
        OrganizationResponse orgResp = null;
        if (product.getManufacturer() != null) {
            orgResp = mapToOrganizationResponse(product.getManufacturer());
        }
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCoordinates(),
                product.getCreationDate(),
                product.getPrice(),
                product.getPartNumber(),
                product.getUnitOfMeasure(),
                orgResp
        );
    }

    /**
     * Конвертируем Organization -> OrganizationResponse
     */
    private OrganizationResponse mapToOrganizationResponse(Organization org) {
        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getEmployeesCount(),
                org.getType()
        );
    }

}