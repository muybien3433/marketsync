package pl.muybien.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.response.UserLoginResponse;
import pl.muybien.keycloak.KeycloakAuthClient;

@Service
@RequiredArgsConstructor
public class LoginHandler {

    private final KeycloakAuthClient keycloakAuthClient;

    public UserLoginResponse handle(UserLoginRequest request) {
        return keycloakAuthClient.login(request);
    }
}
