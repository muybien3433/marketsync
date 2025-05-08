package pl.muybien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //                          Customer
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException e, ServerHttpRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getURI().getPath()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //                          Other
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, ServerHttpRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                400,
                e.getMessage(),
                "BAD_REQUEST",
                r.getURI().getPath()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
