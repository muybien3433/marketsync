package pl.muybien.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IamExceptionHandler {

    @ExceptionHandler(IamUserCreationException.class)
    public ResponseEntity<StandardErrorResponse> handleUserCreation(IamUserCreationException ex) {
        StandardErrorResponse body = new StandardErrorResponse(
                "USER_CREATION_FAILED",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(IamLoginException.class)
    public ResponseEntity<StandardErrorResponse> handleLogin(IamLoginException ex) {
        StandardErrorResponse body = new StandardErrorResponse(
                "LOGIN_FAILED",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
