package soa.duo.ebayservice.controller.advice;

import soa.duo.ebayservice.model.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Ошибки валидации @Valid/@RequestBody/etc.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        // Можно собрать все сообщения об ошибках валидации
        // и вернуть единое сообщение. Для простоты вернём первый.
        String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorDto error = new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                defaultMessage,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Обработка IllegalArgumentException - часто для некорректных параметров
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDto error = new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Пример обработчика на случай, когда ничего не найдено
     * (эквивалент '404' в спецификации).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(ResourceNotFoundException ex) {
        ErrorDto error = new ErrorDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Пример обработчика на случай Service Unavailable (503).
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorDto> handleServiceUnavailable(ServiceUnavailableException ex) {
        ErrorDto error = new ErrorDto(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * "Ловим" все остальные ошибки -> 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleAll(Exception ex) {
        ErrorDto error = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}