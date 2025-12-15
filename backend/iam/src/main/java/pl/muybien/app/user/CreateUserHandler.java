package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.UserCreateRequest;
import pl.muybien.dto.response.UserCreatedResponse;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class CreateUserHandler {

    private final KeycloakUserClient keycloakUserClient;

    public UserCreatedResponse handle(UserCreateRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(Boolean.TRUE);

        return keycloakUserClient.createUser(user);
    }
}
