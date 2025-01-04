package soa.duo.product_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soa.duo.product_service.model.Organization;


@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
}