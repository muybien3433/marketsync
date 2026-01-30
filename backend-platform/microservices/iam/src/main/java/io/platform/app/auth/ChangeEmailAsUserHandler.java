package io.platform.app.auth;

import io.platform.dto.iam.request.KeycloakUserChangeEmailRequest;
import io.platform.dto.iam.response.KeycloakEmailChangedResponse;
import io.platform.keycloak.KeycloakAuthClient;
import io.platform.keycloak.KeycloakUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeEmailAsUserHandler {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserClient keycloakUserClient;

    public KeycloakEmailChangedResponse handle(KeycloakUserChangeEmailRequest request) {
        String username = request.username();
        String password = request.currentPassword();
        String newEmail = request.newEmail();

        keycloakAuthClient.verifyCredentials(username, password);

        return keycloakUserClient.changeEmailByUsername(
                username,
                newEmail,
                Boolean.FALSE
        );
    }
}
