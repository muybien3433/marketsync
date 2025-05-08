package pl.muybien.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.rmi.AccessException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //                          Finance
    @ExceptionHandler(FinanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFinanceNotFoundException(FinanceNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //                          Customer
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //                          Wallet
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFoundException(WalletNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(WalletOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleWalletOwnershipException(WalletOwnershipException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    //                          Asset
    @ExceptionHandler(OwnershipException.class)
    public ResponseEntity<ErrorResponse> handleAssetOwnershipException(OwnershipException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                403,
                e.getMessage(),
                "FORBIDDEN",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssetNotFoundException(AssetNotFoundException e, HttpServletRequest r) {
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
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(jakarta.persistence.EntityNotFoundException e, HttpServletRequest r) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                404,
                e.getMessage(),
                "NOT_FOUND",
                r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

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
