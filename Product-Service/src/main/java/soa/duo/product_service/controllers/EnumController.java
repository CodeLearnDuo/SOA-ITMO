package soa.duo.product_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soa.duo.product_service.model.enums.OrganizationType;
import soa.duo.product_service.model.enums.UnitOfMeasure;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/enums")
public class EnumController {

    @GetMapping("/organization-type")
    public ResponseEntity<?> getOrganizationTypes() {
        try {
            OrganizationType[] organizationTypes = OrganizationType.values();
            return ResponseEntity.ok(organizationTypes);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @GetMapping("/unit-of-measure")
    public ResponseEntity<?> getUnitOfMeasures() {
        try {
            UnitOfMeasure[] unitOfMeasures = UnitOfMeasure.values();
            return ResponseEntity.ok(unitOfMeasures);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Internal server error");
            body.put("time", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
