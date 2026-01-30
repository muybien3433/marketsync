package pl.muybien.service;

import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.request.UserRegisterRequest;

public interface UserService {

    String register(UserRegisterRequest request);
    KeycloakUserLoginResponse login(UserLoginRequest request);
}
