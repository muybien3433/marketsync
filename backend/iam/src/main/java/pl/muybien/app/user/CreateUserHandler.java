package pl.muybien.app.user;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import pl.muybien.account.UserIdentityInput;
import pl.muybien.account.UserIdentityResolution;
import pl.muybien.dto.request.UserCreateRequest;
import pl.muybien.dto.response.UserCreatedResponse;
import pl.muybien.keycloak.KeycloakUserClient;
import pl.muybien.service.AccountIdentityService;

@Service
@RequiredArgsConstructor
public class CreateUserHandler {

    private final KeycloakUserClient keycloakUserClient;
    private final AccountIdentityService accountIdentityService;

    public UserCreatedResponse handle(UserCreateRequest request) {

        UserIdentityResolution identity = accountIdentityService.resolve(
                new UserIdentityInput(request.username(), request.email(), request.firstName(), request.lastName())
        );

        UserRepresentation user = new UserRepresentation();
        user.setUsername(identity.username());
        user.setEmail(identity.email());
        user.setEmailVerified(identity.emailVerified());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(Boolean.TRUE);

        return keycloakUserClient.createUser(user);
    }
}
