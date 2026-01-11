package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.keycloak.KeycloakUserClient;

@Service
@RequiredArgsConstructor
public class DeleteUserHandler {

    private final KeycloakUserClient keycloakUserClient;

    public void handle(String id) {
        keycloakUserClient.deleteUserById(id);
    }
}
