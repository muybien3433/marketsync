package pl.muybien.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handle(ServiceException ex, HttpServletRequest request) {
        int status = ex.getStatus() >= 500 ? 502 : ex.getStatus();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now().toString(),
                status,
                ex.getMessage(),
                ex.getCode(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now().toString(),
                500,
                "Unexpected error",
                "INTERNAL_ERROR",
                request.getRequestURI()
        );

        return ResponseEntity.status(500).body(body);
    }
}
