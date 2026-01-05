package pl.muybien.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.dto.iam.response.KeycloakUserCreatedResponse;
import pl.muybien.dto.user.request.UserRegisterRequest;
import pl.muybien.feign.IamClient;
import pl.muybien.mapper.user.UserMapper;
import pl.muybien.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IamClient iamClient;
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    @Transactional
    public ResponseEntity<Void> register(UserRegisterRequest request) {
        KeycloakUserCreatedResponse response = iamClient.createUser(request.keycloakUserCreateRequest());
        repository.save(mapper.toEntity(request, response.id()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
