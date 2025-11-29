package pl.muybien.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    //                          Finance
    @ExceptionHandler(FinanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCryptoNotFoundException(FinanceNotFoundException e, ServerHttpRequest r) {
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
