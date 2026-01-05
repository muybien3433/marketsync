package pl.muybien.app.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.keycloak.KeycloakRoleClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCompositeRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String roleName, List<String> authorities) {
        roleClient.updateCompositeRole(roleName, authorities);
    }
}
