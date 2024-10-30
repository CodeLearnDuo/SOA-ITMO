package soa.duo.ebay_service.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String message;
    private LocalDateTime time;

    public ErrorResponse(String message) {
        this.message = message;
        this.time = LocalDateTime.now();
    }
}