package pl.muybien.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.dto.iam.request.UserChangeEmailRequest;
import pl.muybien.dto.iam.response.EmailChangedResponse;
import pl.muybien.keycloak.KeycloakAuthClient;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class ChangeEmailAsUserHandler {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserClient keycloakUserClient;

    public EmailChangedResponse handle(UserChangeEmailRequest request) {
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
