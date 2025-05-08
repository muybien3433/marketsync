package pl.muybien.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //                          Kafka
    @ExceptionHandler(TransferServiceException.class)
    public ResponseEntity<ErrorResponse> handleTransferServiceException(TransferServiceException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                500,
                e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    //                          Subscription
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleSubscriptionOwnershipException(OwnershipException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidSubscriptionParametersException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSubscriptionParametersException(InvalidSubscriptionParametersException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                400,
                e.getMessage(),
                "BAD_REQUEST",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSubscriptionNotFoundException(SubscriptionNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //                          Other
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> handleAccessException(AccessException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

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
