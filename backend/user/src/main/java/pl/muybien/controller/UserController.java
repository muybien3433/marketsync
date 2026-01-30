package pl.muybien.controller;

import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.request.UserRegisterRequest;
import pl.muybien.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@Valid @RequestBody UserRegisterRequest request) {
        String userId = service.register(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<KeycloakUserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}
