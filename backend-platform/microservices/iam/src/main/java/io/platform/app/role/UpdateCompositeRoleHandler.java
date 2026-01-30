package io.platform.app.role;

import io.platform.keycloak.KeycloakRoleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCompositeRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String roleName, List<String> authorities) {
        roleClient.updateCompositeRole(roleName, authorities);
    }
}
