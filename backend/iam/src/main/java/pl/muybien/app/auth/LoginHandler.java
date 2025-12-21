package pl.muybien.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.dto.iam.request.KeycloakUserLoginRequest;
import pl.muybien.dto.iam.response.KeycloakUserLoginResponse;
import pl.muybien.keycloak.KeycloakAuthClient;

@Service
@RequiredArgsConstructor
public class LoginHandler {

    private final KeycloakAuthClient keycloakAuthClient;

    public KeycloakUserLoginResponse handle(KeycloakUserLoginRequest request) {
        return keycloakAuthClient.login(request);
    }
}
