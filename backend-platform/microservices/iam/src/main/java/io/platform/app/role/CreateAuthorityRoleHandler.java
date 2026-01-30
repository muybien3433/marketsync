package io.platform.app.role;

import io.platform.keycloak.KeycloakRoleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAuthorityRoleHandler {

    private final KeycloakRoleClient roleClient;

    public void handle(String name) {
        roleClient.createAuthorityRole(name);
    }
}