package soa.duo.product_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import soa.duo.product_service.model.Organization;
import soa.duo.product_service.model.Product;
import soa.duo.product_service.model.enums.UnitOfMeasure;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    public boolean existsByPartNumber(String partNumber);

    @Query("SELECT SUM(p.price) FROM Product p")
    Double calculateTotalPrice();

    @Query("SELECT DISTINCT p.manufacturer FROM Product p WHERE p.manufacturer IS NOT NULL")
    List<Organization> findDistinctManufacturers();

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.price = p.price * (1 + :percent / 100.0)")
    void increasePricesForAllProducts(@Param("percent") double percent);

    List<Product> findByUnitOfMeasure(UnitOfMeasure unitOfMeasure);

}
