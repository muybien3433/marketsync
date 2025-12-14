package pl.muybien.service.user;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.muybien.dto.request.IamUserCreateRequest;
import pl.muybien.dto.response.IamUserCreatedResponse;
import pl.muybien.exception.IamUserCreationException;

@Service
public class IamUserServiceImpl implements IamUserService {

    private final Keycloak keycloak;
    private final String realm;

    public IamUserServiceImpl(
            Keycloak keycloak,
            @Value("${keycloak.realm}") String realm
    ) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    @Override
    public IamUserCreatedResponse createUser(IamUserCreateRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled());

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        try (Response response = usersResource.create(user)) {
            int status = response.getStatus();

            if (status == 201) {
                String locationPath = response.getLocation().getPath();
                String id = locationPath.substring(locationPath.lastIndexOf('/') + 1);
                return new IamUserCreatedResponse(id, request.username());
            }

            if (status == 400) {
                throw new IamUserCreationException("Invalid user data sent to Keycloak", status);
            }

            if (status == 403) {
                throw new IamUserCreationException("Forbidden to create users in this realm", status);
            }

            if (status == 409) {
                throw new IamUserCreationException("User already exists in Keycloak", status);
            }

            if (status >= 500) {
                throw new IamUserCreationException("Keycloak internal error during user creation", status);
            }

            throw new IamUserCreationException("Unexpected response from Keycloak. Status: " + status, status);
        }
    }
}
