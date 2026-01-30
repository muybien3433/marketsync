package io.platform.app.user;

import io.platform.account.UserIdentityInput;
import io.platform.account.UserIdentityResolution;
import io.platform.dto.iam.request.KeycloakAdminCreateUserWithoutPasswordRequest;
import io.platform.dto.iam.response.KeycloakUserCreatedResponse;
import io.platform.keycloak.KeycloakUserClient;
import io.platform.security.AccountIdentityService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserWithoutPasswordHandler {

    private final KeycloakUserClient keycloakUserClient;
    private final AccountIdentityService accountIdentityService;

    public KeycloakUserCreatedResponse handle(KeycloakAdminCreateUserWithoutPasswordRequest request) {

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
