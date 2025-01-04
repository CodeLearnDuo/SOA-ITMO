package soa.duo.ebay_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DefaultErrorResponse {
    private int code;
    private String message;

    @JsonProperty("time")
    private String time;

    public DefaultErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        ZoneId zoneId = ZoneId.of("UTC+3");
        this.time = ZonedDateTime.ofInstant(Instant.now(), zoneId)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
}

