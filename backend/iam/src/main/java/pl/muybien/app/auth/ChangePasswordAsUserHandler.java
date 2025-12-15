package pl.muybien.app.auth;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.UserChangePasswordRequest;
import pl.muybien.keycloak.KeycloakAuthClient;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class ChangePasswordAsUserHandler {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserClient keycloakUserClient;

    public void handle(UserChangePasswordRequest request) {
        keycloakAuthClient.verifyCredentials(request.username(), request.currentPassword());
        String userId = keycloakUserClient.findUserIdByUsername(request.username());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(request.newPassword());

        keycloakUserClient.resetPassword(userId, credential);
    }
}
