package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.dto.request.IamUserLoginRequest;
import pl.muybien.dto.response.IamUserLoginResponse;
import pl.muybien.service.auth.IamAuthService;

@RestController
@RequestMapping("/api/v1/iam/auth")
@RequiredArgsConstructor
public class IamAuthController {

    private final IamAuthService iamAuthService;

    @PostMapping("/login")
    public ResponseEntity<IamUserLoginResponse> login(@Valid @RequestBody IamUserLoginRequest request) {
        IamUserLoginResponse response = iamAuthService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
