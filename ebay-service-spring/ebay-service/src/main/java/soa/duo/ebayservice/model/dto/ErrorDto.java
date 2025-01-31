package soa.duo.ebayservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {

    private int code;
    private String message;

    // Чтобы поле time соответствовало примеру '2024-09-26T14:30:10Z',
    // можно хранить в строке или использовать LocalDateTime c паттерном ISO.
    // Здесь показываем вариант с LocalDateTime и форматом.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime time;

}