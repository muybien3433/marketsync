package io.platform.app.role;

import io.platform.keycloak.KeycloakRoleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateCompositeRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String roleName, List<String> authorities) {
        roleClient.createCompositeRole(roleName, authorities);
    }
}
