package pl.muybien.service;

import org.springframework.http.ResponseEntity;
import pl.muybien.dto.user.request.UserRegisterRequest;

public interface UserService {

    ResponseEntity<Void> register(UserRegisterRequest request);
}
