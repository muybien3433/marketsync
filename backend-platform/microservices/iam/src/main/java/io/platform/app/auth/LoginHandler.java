package io.platform.app.auth;

import io.platform.dto.iam.request.KeycloakUserLoginRequest;
import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import io.platform.keycloak.KeycloakAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginHandler {

    private final KeycloakAuthClient keycloakAuthClient;

    public KeycloakUserLoginResponse handle(KeycloakUserLoginRequest request) {
        return keycloakAuthClient.login(request);
    }
}
