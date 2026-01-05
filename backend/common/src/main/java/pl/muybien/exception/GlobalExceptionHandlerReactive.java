package pl.muybien.exception;

import java.time.OffsetDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerReactive {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handle(ServiceException ex, ServerWebExchange exchange) {
        int status = ex.getStatus() >= 500 ? 502 : ex.getStatus();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now().toString(),
                status,
                ex.getMessage(),
                ex.getCode(),
                exchange.getRequest().getPath().value()
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled exception on {}", exchange.getRequest().getURI(), ex);

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now().toString(),
                500,
                "Unexpected error",
                "INTERNAL_ERROR",
                exchange.getRequest().getPath().value()
        );

        return ResponseEntity.status(500).body(body);
    }
}
