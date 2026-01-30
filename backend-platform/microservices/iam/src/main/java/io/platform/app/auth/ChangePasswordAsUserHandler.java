package io.platform.app.auth;

import io.platform.dto.iam.request.KeycloakUserChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
import io.platform.keycloak.KeycloakAuthClient;
import io.platform.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class ChangePasswordAsUserHandler {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserClient keycloakUserClient;

    public void handle(KeycloakUserChangePasswordRequest request) {
        keycloakAuthClient.verifyCredentials(request.username(), request.currentPassword());
        String userId = keycloakUserClient.findUserIdByUsername(request.username());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(request.newPassword());

        keycloakUserClient.resetPassword(userId, credential);
    }
}
