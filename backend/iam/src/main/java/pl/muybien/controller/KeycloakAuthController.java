package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.app.auth.ChangeEmailAsUserHandler;
import pl.muybien.app.auth.LoginHandler;
import pl.muybien.app.auth.ChangePasswordAsUserHandler;
import pl.muybien.dto.iam.request.KeycloakUserChangeEmailRequest;
import pl.muybien.dto.iam.request.KeycloakUserLoginRequest;
import pl.muybien.dto.iam.request.KeycloakUserChangePasswordRequest;
import pl.muybien.dto.iam.response.KeycloakEmailChangedResponse;
import pl.muybien.dto.iam.response.KeycloakUserLoginResponse;

@RestController
@RequestMapping("/api/v1/iam/auth")
@RequiredArgsConstructor
public class KeycloakAuthController {

    private final LoginHandler loginHandler;
    private final ChangeEmailAsUserHandler  changeEmailAsUserHandler;
    private final ChangePasswordAsUserHandler changePasswordAsUserHandler;

    @PostMapping("/login")
    public ResponseEntity<KeycloakUserLoginResponse> login(@Valid @RequestBody KeycloakUserLoginRequest request) {
        return ResponseEntity.ok(loginHandler.handle(request));
    }

    @PostMapping("/change-email")
    public ResponseEntity<KeycloakEmailChangedResponse> changeEmail(
            @Valid @RequestBody KeycloakUserChangeEmailRequest request) {
        return ResponseEntity.ok(changeEmailAsUserHandler.handle(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody KeycloakUserChangePasswordRequest request) {
        changePasswordAsUserHandler.handle(request);
        return ResponseEntity.noContent().build();
    }
}
