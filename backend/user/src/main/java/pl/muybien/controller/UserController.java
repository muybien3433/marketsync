package pl.muybien.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.dto.user.request.UserRegisterRequest;
import pl.muybien.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    public ResponseEntity<Void> register(UserRegisterRequest request) {
        return service.register(request);
    }
}
