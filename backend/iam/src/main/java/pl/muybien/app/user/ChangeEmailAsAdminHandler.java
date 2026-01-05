package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.dto.iam.request.KeycloakAdminChangeEmailRequest;
import pl.muybien.dto.iam.response.KeycloakEmailChangedResponse;
import pl.muybien.keycloak.KeycloakUserClient;

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
