package pl.muybien.api;

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
import pl.muybien.dto.iam.response.EmailChangedResponse;
import pl.muybien.dto.iam.response.UserCreatedResponse;

@RestController
@RequestMapping("/api/v1/iam/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserHandler createUserHandler;
    private final CreateUserWithPasswordHandler createUserWithPasswordHandler;
    private final CreateUserWithoutPasswordHandler createUserWithoutPasswordHandler;
    private final ChangeEmailAsAdminHandler changeEmailAsAdminHandler;
    private final ChangePasswordAsAdminHandler changePasswordAsAdminHandler;

    @PostMapping
    public ResponseEntity<UserCreatedResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserHandler.handle(request));
    }

    @PostMapping("/with-password")
    public ResponseEntity<UserCreatedResponse> createUserWithPassword(
            @Valid @RequestBody AdminCreateUserWithPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserWithPasswordHandler.handle(request));
    }

    @PostMapping("/without-password")
    public ResponseEntity<UserCreatedResponse> createUserWithoutPassword(
            @Valid @RequestBody AdminCreateUserWithoutPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserWithoutPasswordHandler.handle(request));
    }

    @PostMapping("/change-email-admin")
    public ResponseEntity<EmailChangedResponse> changeEmailAsAdmin(
            @Valid @RequestBody AdminChangeEmailRequest request) {
        return ResponseEntity.ok(changeEmailAsAdminHandler.handle(request));
    }

    @PostMapping("/change-password-admin")
    public ResponseEntity<Void> changePasswordAsAdmin(@Valid @RequestBody AdminChangePasswordRequest request) {
        changePasswordAsAdminHandler.handle(request);
        return ResponseEntity.noContent().build();
    }
}
