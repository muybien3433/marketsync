package pl.muybien.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //                          Notification
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServiceNotFoundException(ServiceNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                400,
                e.getMessage(),
                "BAD_REQUEST",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMessageNotSendException(MessageNotSendException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                500,
                e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleSupportDetailsNotFoundException(SupportDetailsNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                500,
                e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    //                          Other
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                400,
                e.getMessage(),
                "BAD_REQUEST",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
