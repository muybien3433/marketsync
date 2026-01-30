package io.platform.app.user;

import io.platform.account.UserIdentityInput;
import io.platform.account.UserIdentityResolution;
import io.platform.dto.iam.request.KeycloakAdminCreateUserWithPasswordRequest;
import io.platform.dto.iam.response.KeycloakUserCreatedResponse;
import io.platform.keycloak.KeycloakUserClient;
import io.platform.security.AccountIdentityService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserWithPasswordHandler {

    private final KeycloakUserClient keycloakUserClient;
    private final AccountIdentityService accountIdentityService;

    public KeycloakUserCreatedResponse handle(KeycloakAdminCreateUserWithPasswordRequest request) {

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

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        user.setCredentials(Collections.singletonList(credential));

        return keycloakUserClient.createUser(user);
    }
}