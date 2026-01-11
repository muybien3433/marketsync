package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private final DeleteUserHandler deleteUserHandler;

    @PostMapping
    public ResponseEntity<KeycloakUserCreatedResponse> create(@Valid @RequestBody KeycloakUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserHandler.handle(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUserHandler.handle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/with-password")
    public ResponseEntity<KeycloakUserCreatedResponse> createWithPassword(
            @Valid @RequestBody KeycloakAdminCreateUserWithPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserWithPasswordHandler.handle(request));
    }

    @PostMapping("/without-password")
    public ResponseEntity<KeycloakUserCreatedResponse> createWithoutPassword(
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
