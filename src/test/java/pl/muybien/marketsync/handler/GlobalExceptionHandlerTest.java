package pl.muybien.marketsync.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleEntityNotFoundException() {
        String message = "Customer with email test@example.com not found.";
        EntityNotFoundException exception = new EntityNotFoundException(message);

        ResponseEntity<String> response = globalExceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void handleAccessException() {
        String message = "Access denied.";
        java.rmi.AccessException exception = new java.rmi.AccessException(message);

        ResponseEntity<String> response = globalExceptionHandler.handleAccessException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void handleCryptoNotFoundException() {
        String message = "No service found for type: Bitcoin";
        CryptoNotFoundException exception = new CryptoNotFoundException(message);

        ResponseEntity<String> response = globalExceptionHandler.handleCryptoNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void handleAccessDeniedException() {
        String message = "Access denied.";
        AccessDeniedException exception = new AccessDeniedException(message);

        ResponseEntity<String> response = globalExceptionHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void handleInvalidSubscriptionParametersException() {
        String message = "Invalid subscription parameters.";
        InvalidSubscriptionParametersException exception = new InvalidSubscriptionParametersException(message);

        ResponseEntity<String> response = globalExceptionHandler.handleInvalidSubscriptionParametersException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}
