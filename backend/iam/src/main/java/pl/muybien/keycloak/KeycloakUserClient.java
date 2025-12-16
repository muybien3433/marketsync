package pl.muybien.keycloak;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.muybien.dto.response.EmailChangedResponse;
import pl.muybien.dto.response.UserCreatedResponse;
import pl.muybien.exception.PasswordChangeException;
import pl.muybien.exception.UserCreationException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class KeycloakUserClient {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserClient(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public UserCreatedResponse createUser(UserRepresentation user) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource resource = realmResource.users();

        try (Response response = resource.create(user)) {
            int status = response.getStatus();

            if (status == 201) {
                String locationPath = response.getLocation().getPath();
                String id = locationPath.substring(locationPath.lastIndexOf('/') + 1);
                return new UserCreatedResponse(id, user.getUsername());
            }

            if (status == 400) {
                throw new UserCreationException("Invalid user data sent to Keycloak", status);
            }

            if (status == 403) {
                throw new UserCreationException("Forbidden to create users in this realm", status);
            }

            if (status == 409) {
                throw new UserCreationException("User already exists in Keycloak", status);
            }

            if (status >= 500) {
                throw new UserCreationException("Internal error during user creation", status);
            }

            throw new UserCreationException("Unexpected response from Keycloak. Status: " + status, status);
        }
    }

    public void resetPassword(String userId, CredentialRepresentation credential) {
        UserResource user = keycloak.realm(realm).users().get(userId);
        try {
            user.resetPassword(credential);
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;
            if (status == 404) {
                throw new PasswordChangeException("User not found in Keycloak", status);
            }
            if (status == 400) {
                throw new PasswordChangeException("Invalid password payload for Keycloak", status);
            }
            throw new PasswordChangeException("Failed to change password in Keycloak. Status: " + status, status);
        }
    }

    public String findUserIdByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm).users().search(username, true);
        if (users.isEmpty()) {
            throw new PasswordChangeException("User not found: " + username, 404);
        }

        return users.getFirst().getId();
    }

    public boolean isUsernameAvailable(String username) {
        String u = username != null ? username.trim() : "";
        if (u.isEmpty()) return false;

        UsersResource usersResource = keycloak.realm(realm).users();

        try {
            List<UserRepresentation> exact = usersResource.search(u, true);
            if (exact == null || exact.isEmpty()) return true;

            return exact.stream()
                    .map(UserRepresentation::getUsername)
                    .filter(Objects::nonNull)
                    .noneMatch(found -> found.equalsIgnoreCase(u));
        } catch (WebApplicationException ex) {
            List<UserRepresentation> results = usersResource.search(u);
            if (results == null || results.isEmpty()) return true;

            String needle = u.toLowerCase(Locale.ROOT);
            return results.stream()
                    .map(UserRepresentation::getUsername)
                    .filter(Objects::nonNull)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .noneMatch(needle::equals);
        }
    }

    public EmailChangedResponse changeEmailByUsername(String username, String newEmail, Boolean emailVerified) {
        String userId = findUserIdByUsername(username);
        changeEmailByUserId(userId, newEmail, emailVerified);

        UserRepresentation updated = getUserById(userId);
        return new EmailChangedResponse(
                userId,
                updated.getUsername(),
                updated.getEmail(),
                Boolean.TRUE.equals(updated.isEmailVerified())
        );
    }

    public void changeEmailByUserId(String userId, String newEmail, Boolean emailVerified) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new PasswordChangeException("User id is required", 400);
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new PasswordChangeException("New email is required", 400);
        }

        UserResource userResource = keycloak.realm(realm).users().get(userId);

        try {
            UserRepresentation rep = userResource.toRepresentation();
            rep.setEmail(newEmail.trim());
            if (emailVerified != null) {
                rep.setEmailVerified(emailVerified);
            }
            userResource.update(rep);
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

            if (status == 404) {
                throw new PasswordChangeException("User not found in Keycloak", status);
            }
            if (status == 400) {
                throw new PasswordChangeException("Invalid email payload for Keycloak", status);
            }
            if (status == 409) {
                throw new PasswordChangeException("Email already exists in Keycloak", status);
            }

            throw new PasswordChangeException("Failed to change email in Keycloak. Status: " + status, status);
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;
            if (status == 404) {
                throw new PasswordChangeException("User not found in Keycloak", status);
            }
            throw new PasswordChangeException("Failed to read user from Keycloak. Status: " + status, status);
        }
    }
}
