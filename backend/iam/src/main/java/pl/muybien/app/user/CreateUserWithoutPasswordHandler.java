package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.account.UserIdentityInput;
import pl.muybien.account.UserIdentityResolution;
import pl.muybien.dto.iam.request.AdminCreateUserWithoutPasswordRequest;
import pl.muybien.dto.iam.response.UserCreatedResponse;
import pl.muybien.keycloak.KeycloakUserClient;
import pl.muybien.service.AccountIdentityService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserWithoutPasswordHandler {

    private final KeycloakUserClient keycloakUserClient;
    private final AccountIdentityService accountIdentityService;

    public UserCreatedResponse handle(AdminCreateUserWithoutPasswordRequest request) {

        UserIdentityResolution identity = accountIdentityService.resolve(
                new UserIdentityInput(request.username(), request.email(), request.firstName(), request.lastName())
        );

        UserRepresentation user = new UserRepresentation();
        user.setUsername(identity.username());
        user.setEmail(identity.email());
        user.setEmailVerified(identity.emailVerified());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled() != null ? request.enabled() : Boolean.TRUE);
        user.setCredentials(Collections.emptyList());

        return keycloakUserClient.createUser(user);
    }
}
