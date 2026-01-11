package pl.muybien.service;

import pl.muybien.dto.iam.response.KeycloakUserLoginResponse;
import pl.muybien.dto.user.request.UserLoginRequest;
import pl.muybien.dto.user.request.UserRegisterRequest;

public interface UserService {

    String register(UserRegisterRequest request);
    KeycloakUserLoginResponse login(UserLoginRequest request);
}
