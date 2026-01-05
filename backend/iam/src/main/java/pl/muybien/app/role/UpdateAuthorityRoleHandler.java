package pl.muybien.app.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.keycloak.KeycloakRoleClient;

@Service
@RequiredArgsConstructor
public class UpdateAuthorityRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String currentName, String newName) {
        roleClient.updateAuthorityRole(currentName, newName);
    }
}
