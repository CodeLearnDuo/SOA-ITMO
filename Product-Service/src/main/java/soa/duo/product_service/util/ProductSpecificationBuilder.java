package soa.duo.product_service.util;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import soa.duo.product_service.model.Product;

import java.time.LocalDateTime;
import java.util.*;

public class ProductSpecificationBuilder {

    public static Specification<Product> buildSpecificationFromFilters(List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        Specification<Product> spec = Specification.where(null);

        for (String filter : filters) {
            int operatorStart = filter.indexOf('[');
            int operatorEnd = filter.indexOf(']');
            if (operatorStart < 0 || operatorEnd < 0 || operatorEnd <= operatorStart) {
                throw new IllegalArgumentException("Invalid filter format: " + filter);
            }
            String field = filter.substring(0, operatorStart);
            String operator = filter.substring(operatorStart + 1, operatorEnd);
            String value = filter.substring(operatorEnd + 1);
            if (value.startsWith("=")) {
                value = value.substring(1);
            }

            if (!isValidFilterField(field)) {
                throw new IllegalArgumentException("Invalid filter field: '" + field + "'");
            }
            if (!isValidOperator(operator)) {
                throw new IllegalArgumentException("Invalid operator: '" + operator + "'");
            }

            Specification<Product> filterSpec = buildSpecificationForFilter(field, operator, value);
            spec = spec.and(filterSpec);
        }

        return spec;
    }

    public static boolean isValidSortField(String field) {
        Set<String> validFields = new HashSet<>(Arrays.asList(
                "id", "name", "creationDate", "price", "partNumber", "unitOfMeasure",
                "coordinates.x", "coordinates.y",
                "manufacturer.id", "manufacturer.name", "manufacturer.employeesCount", "manufacturer.type"
        ));

        return validFields.contains(field);
    }

    public static String mapField(String field) {
        Map<String, String> fieldMapping = new HashMap<>();

        fieldMapping.put("id", "id");
        fieldMapping.put("name", "name");
        fieldMapping.put("creationDate", "creationDate");
        fieldMapping.put("price", "price");
        fieldMapping.put("partNumber", "partNumber");
        fieldMapping.put("unitOfMeasure", "unitOfMeasure");

        fieldMapping.put("coordinates.x", "coordinates.x");
        fieldMapping.put("coordinates.y", "coordinates.y");

        fieldMapping.put("manufacturer.id", "manufacturer.id");
        fieldMapping.put("manufacturer.name", "manufacturer.name");
        fieldMapping.put("manufacturer.employeesCount", "manufacturer.employeesCount");
        fieldMapping.put("manufacturer.type", "manufacturer.organizationType");

        fieldMapping.put("substring", "name");

        String mappedField = fieldMapping.get(field);
        if (mappedField == null) {
            throw new IllegalArgumentException("Invalid field: '" + field + "'");
        }
        return mappedField;
    }

    private static boolean isValidFilterField(String field) {
        Set<String> validFields = new HashSet<>(Arrays.asList(
                "id", "name", "creationDate", "price", "partNumber", "unitOfMeasure",
                "coordinates.x", "coordinates.y",
                "manufacturer.id", "manufacturer.name", "manufacturer.employeesCount", "manufacturer.type",
                "substring"
        ));

        return validFields.contains(field);
    }

    private static boolean isValidOperator(String operator) {
        Set<String> validOperators = new HashSet<>(Arrays.asList("eq", "ne", "gt", "lt", "gte", "lte"));
        return validOperators.contains(operator);
    }

    private static Specification<Product> buildSpecificationForFilter(String field, String operator, String value) {
        return (root, query, criteriaBuilder) -> {
            if (field.equals("substring")) {
                return criteriaBuilder.like(root.get("name"), "%" + value + "%");
            }

            Path<?> path = getPath(root, field);
            Object parsedValue = parseValue(path.getJavaType(), value);

            switch (operator) {
                case "eq":
                    return criteriaBuilder.equal(path, parsedValue);
                case "ne":
                    return criteriaBuilder.notEqual(path, parsedValue);
                case "gt":
                    return criteriaBuilder.greaterThan((Expression<Comparable>) path, (Comparable) parsedValue);
                case "lt":
                    return criteriaBuilder.lessThan((Expression<Comparable>) path, (Comparable) parsedValue);
                case "gte":
                    return criteriaBuilder.greaterThanOrEqualTo((Expression<Comparable>) path, (Comparable) parsedValue);
                case "lte":
                    return criteriaBuilder.lessThanOrEqualTo((Expression<Comparable>) path, (Comparable) parsedValue);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        };
    }

    private static Path<?> getPath(Root<Product> root, String field) {
        String mappedField = mapField(field);
        String[] parts = mappedField.split("\\.");
        From<?, ?> from = root;
        Path<?> path = null;

        for (String part : parts) {
            if (part.equals("manufacturer")) {
                from = from.join("manufacturer", JoinType.LEFT);
                path = from;
            } else if (part.equals("coordinates")) {
                path = (path == null) ? from.get(part) : path.get(part);
            } else {
                path = (path == null) ? from.get(part) : path.get(part);
            }
        }

        return path;
    }

    private static Object parseValue(Class<?> type, String value) {
        try {
            if (type.equals(String.class)) {
                return value;
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.valueOf(value);
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                return Long.valueOf(value);
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.valueOf(value);
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                return Float.valueOf(value);
            } else if (type.equals(LocalDateTime.class)) {
                return LocalDateTime.parse(value);
            } else if (Enum.class.isAssignableFrom(type)) {
                return Enum.valueOf((Class<Enum>) type, value);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type.getName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value for type " + type.getSimpleName() + ": " + value);
        }
    }
}