package pl.muybien.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.app.auth.LoginHandler;
import pl.muybien.app.auth.ChangePasswordAsUserHandler;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.request.UserChangePasswordRequest;
import pl.muybien.dto.response.UserLoginResponse;

@RestController
@RequestMapping("/api/v1/iam/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginHandler loginHandler;
    private final ChangePasswordAsUserHandler changePasswordAsUserHandler;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(loginHandler.handle(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody UserChangePasswordRequest request) {
        changePasswordAsUserHandler.handle(request);
        return ResponseEntity.noContent().build();
    }
}
