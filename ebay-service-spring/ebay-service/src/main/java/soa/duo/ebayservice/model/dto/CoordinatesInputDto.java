package soa.duo.ebayservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesInputDto {

    private @NotNull(message = "x coordinate must not be null") Integer x;

    @Max(value = 398, message = "y coordinate must be <= 398")
    private int y;

}