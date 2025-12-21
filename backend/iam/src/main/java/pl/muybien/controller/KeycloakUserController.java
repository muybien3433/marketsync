package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.app.user.*;
import pl.muybien.dto.iam.request.*;
import pl.muybien.dto.iam.response.KeycloakEmailChangedResponse;
import pl.muybien.dto.iam.response.KeycloakUserCreatedResponse;

@RestController
@RequestMapping("/api/v1/iam/users")
@RequiredArgsConstructor
public class KeycloakUserController {

    private final CreateUserHandler createUserHandler;
    private final CreateUserWithPasswordHandler createUserWithPasswordHandler;
    private final CreateUserWithoutPasswordHandler createUserWithoutPasswordHandler;
    private final ChangeEmailAsAdminHandler changeEmailAsAdminHandler;
    private final ChangePasswordAsAdminHandler changePasswordAsAdminHandler;

    @PostMapping
    public ResponseEntity<KeycloakUserCreatedResponse> createUser(@Valid @RequestBody KeycloakUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserHandler.handle(request));
    }

    @PostMapping("/with-password")
    public ResponseEntity<KeycloakUserCreatedResponse> createUserWithPassword(
            @Valid @RequestBody KeycloakAdminCreateUserWithPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserWithPasswordHandler.handle(request));
    }

    @PostMapping("/without-password")
    public ResponseEntity<KeycloakUserCreatedResponse> createUserWithoutPassword(
            @Valid @RequestBody KeycloakAdminCreateUserWithoutPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserWithoutPasswordHandler.handle(request));
    }

    @PostMapping("/change-email-admin")
    public ResponseEntity<KeycloakEmailChangedResponse> changeEmailAsAdmin(
            @Valid @RequestBody KeycloakAdminChangeEmailRequest request) {
        return ResponseEntity.ok(changeEmailAsAdminHandler.handle(request));
    }

    @PostMapping("/change-password-admin")
    public ResponseEntity<Void> changePasswordAsAdmin(@Valid @RequestBody KeycloakAdminChangePasswordRequest request) {
        changePasswordAsAdminHandler.handle(request);
        return ResponseEntity.noContent().build();
    }
}
