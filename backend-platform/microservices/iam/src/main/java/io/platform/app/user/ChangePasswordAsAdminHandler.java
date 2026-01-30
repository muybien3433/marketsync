package io.platform.app.user;

import io.platform.dto.iam.request.KeycloakAdminChangePasswordRequest;
import io.platform.keycloak.KeycloakUserClient;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordAsAdminHandler {

    private final KeycloakUserClient keycloakUserClient;

    public void handle(KeycloakAdminChangePasswordRequest request) {
        String userId = keycloakUserClient.findUserIdByUsername(request.username());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(request.temporary() != null ? request.temporary() : Boolean.FALSE);
        credential.setValue(request.newPassword());

        keycloakUserClient.resetPassword(userId, credential);
    }
}
