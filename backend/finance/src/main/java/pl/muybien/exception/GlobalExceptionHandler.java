package pl.muybien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.rmi.AccessException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //                          Finance
    @ExceptionHandler(FinanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFinanceNotFoundException(FinanceNotFoundException e, ServerHttpRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                500,
                e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                r.getURI().getPath()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    //                          Other
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> handleAccessException(AccessException e, ServerHttpRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getURI().getPath()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

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
