package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.dto.request.IamUserCreateRequest;
import pl.muybien.dto.response.IamUserCreatedResponse;
import pl.muybien.service.user.IamUserService;

@RestController
@RequestMapping("/api/v1/iam/users")
@RequiredArgsConstructor
public class IamUserController {

    private final IamUserService iamUserService;

    @PostMapping
    public ResponseEntity<IamUserCreatedResponse> createUser(@Valid @RequestBody IamUserCreateRequest request) {
        IamUserCreatedResponse response = iamUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
