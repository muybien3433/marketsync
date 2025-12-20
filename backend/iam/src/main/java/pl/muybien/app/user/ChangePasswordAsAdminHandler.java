package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.dto.iam.request.AdminChangePasswordRequest;
import pl.muybien.keycloak.KeycloakAuthClient;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class ChangePasswordAsAdminHandler {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserClient keycloakUserClient;

    public void handle(AdminChangePasswordRequest request) {
        String userId = keycloakUserClient.findUserIdByUsername(request.username());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(request.temporary() != null ? request.temporary() : Boolean.FALSE);
        credential.setValue(request.newPassword());

        keycloakUserClient.resetPassword(userId, credential);
    }
}
