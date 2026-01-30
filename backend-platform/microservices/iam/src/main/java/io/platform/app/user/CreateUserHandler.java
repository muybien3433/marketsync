package io.platform.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import io.platform.account.UserIdentityInput;
import io.platform.account.UserIdentityResolution;
import io.platform.dto.iam.request.KeycloakUserCreateRequest;
import io.platform.dto.iam.response.KeycloakUserCreatedResponse;
import io.platform.keycloak.KeycloakUserClient;
import io.platform.security.AccountIdentityService;

@Service
@RequiredArgsConstructor
public class CreateUserHandler {

    private final KeycloakUserClient keycloakUserClient;
    private final AccountIdentityService accountIdentityService;

    public KeycloakUserCreatedResponse handle(KeycloakUserCreateRequest request) {

        UserIdentityResolution identity = accountIdentityService.resolve(
                new UserIdentityInput(request.username(), request.email(), request.firstName(), request.lastName())
        );

        UserRepresentation user = new UserRepresentation();
        user.setUsername(identity.username());
        user.setEmail(identity.email());
        user.setEmailVerified(identity.emailVerified());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(Boolean.TRUE);

        return keycloakUserClient.createUser(user);
    }
}
