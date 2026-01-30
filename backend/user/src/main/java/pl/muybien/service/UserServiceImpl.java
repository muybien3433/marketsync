package pl.muybien.service;

import io.platform.dto.iam.request.KeycloakUserLoginRequest;
import io.platform.dto.iam.response.KeycloakUserCreatedResponse;
import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.request.UserRegisterRequest;
import pl.muybien.feign.IamClient;
import pl.muybien.mapper.UserMapper;
import pl.muybien.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final IamClient iamClient;
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    @Transactional
    public String register(UserRegisterRequest request) {
        KeycloakUserCreatedResponse response = iamClient.register(request.userCreateRequest());
        try {
            repository.saveAndFlush(mapper.toEntity(request, response.id()));
            return response.id();
        } catch (RuntimeException e) {
            try {
                iamClient.delete(response.id());
            } catch (Exception deleteEx) {
                log.error("Failed to delete user in Keycloak, id={}", response.id(), deleteEx);
            }

            throw e;
        }
    }

    @Override
    public KeycloakUserLoginResponse login(UserLoginRequest request) {
        return iamClient.login(new KeycloakUserLoginRequest(request.username(), request.password()));
    }
}
