package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.AdminCreateUserWithoutPasswordRequest;
import pl.muybien.dto.response.UserCreatedResponse;
import pl.muybien.keycloak.KeycloakUserClient;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserWithoutPasswordHandler {

    private final KeycloakUserClient keycloakUserClient;

    public UserCreatedResponse handle(AdminCreateUserWithoutPasswordRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled() != null ? request.enabled() : Boolean.TRUE);
        user.setCredentials(Collections.emptyList());
        user.setEmailVerified(request.emailVerified() != null ? request.enabled() : Boolean.FALSE);

        return keycloakUserClient.createUser(user);
    }
}
