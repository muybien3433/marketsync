package io.platform.app.role;

import io.platform.keycloak.KeycloakRoleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAuthorityRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String currentName, String newName) {
        roleClient.updateAuthorityRole(currentName, newName);
    }
}
