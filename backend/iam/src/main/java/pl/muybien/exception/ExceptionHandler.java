package pl.muybien.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.muybien.dto.response.StandardErrorResponse;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(UserCreationException.class)
    public ResponseEntity<StandardErrorResponse> handleUserCreation(UserCreationException ex) {
        StandardErrorResponse body = new StandardErrorResponse(
                "USER_CREATION_FAILED",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(LoginException.class)
    public ResponseEntity<StandardErrorResponse> handleUserLogin(LoginException ex) {
        StandardErrorResponse body = new StandardErrorResponse(
                "USER_LOGIN_FAILED",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<StandardErrorResponse> handlePasswordChange(PasswordChangeException ex) {
        StandardErrorResponse body = new StandardErrorResponse(
                "PASSWORD_CHANGE_FAILED",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
