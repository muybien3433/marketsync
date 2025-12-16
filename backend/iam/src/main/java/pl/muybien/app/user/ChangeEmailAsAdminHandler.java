package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.AdminChangeEmailRequest;
import pl.muybien.dto.response.EmailChangedResponse;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class ChangeEmailAsAdminHandler {

    private final KeycloakUserClient keycloakUserClient;

    public EmailChangedResponse handle(AdminChangeEmailRequest request) {
        return keycloakUserClient.changeEmailByUsername(
                request.username(),
                request.newEmail(),
                request.emailVerified()
        );
    }
}
