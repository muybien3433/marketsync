package io.platform.controller;

import io.platform.app.auth.ChangeEmailAsUserHandler;
import io.platform.app.auth.ChangePasswordAsUserHandler;
import io.platform.app.auth.LoginHandler;
import io.platform.dto.iam.request.KeycloakUserChangeEmailRequest;
import io.platform.dto.iam.request.KeycloakUserChangePasswordRequest;
import io.platform.dto.iam.request.KeycloakUserLoginRequest;
import io.platform.dto.iam.response.KeycloakEmailChangedResponse;
import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam/auth")
@RequiredArgsConstructor
public class KeycloakAuthController {

    private final LoginHandler loginHandler;
    private final ChangeEmailAsUserHandler changeEmailAsUserHandler;
    private final ChangePasswordAsUserHandler changePasswordAsUserHandler;

    @PostMapping("/login")
    public ResponseEntity<KeycloakUserLoginResponse> login(@Valid @RequestBody KeycloakUserLoginRequest request) {
        return ResponseEntity.ok(loginHandler.handle(request));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<>

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
