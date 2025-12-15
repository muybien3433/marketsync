package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.AdminCreateUserWithPasswordRequest;
import pl.muybien.dto.response.UserCreatedResponse;
import pl.muybien.keycloak.KeycloakUserClient;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserWithPasswordHandler {

    private final KeycloakUserClient keycloakUserClient;

    public UserCreatedResponse handle(AdminCreateUserWithPasswordRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled() != null ? request.enabled() : Boolean.TRUE);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        user.setCredentials(Collections.singletonList(credential));

        return keycloakUserClient.createUser(user);
    }
}