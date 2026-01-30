package io.platform.app.user;

import io.platform.keycloak.KeycloakUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserHandler {

    private final KeycloakUserClient keycloakUserClient;

    public void handle(String id) {
        keycloakUserClient.deleteUserById(id);
    }
}
