package io.platform.app.user;

import io.platform.dto.iam.request.KeycloakAdminChangeEmailRequest;
import io.platform.dto.iam.response.KeycloakEmailChangedResponse;
import io.platform.keycloak.KeycloakUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeEmailAsAdminHandler {

    private final KeycloakUserClient keycloakUserClient;

    public KeycloakEmailChangedResponse handle(KeycloakAdminChangeEmailRequest request) {
        return keycloakUserClient.changeEmailByUsername(
                request.username(),
                request.newEmail(),
                request.emailVerified()
        );
    }
}
