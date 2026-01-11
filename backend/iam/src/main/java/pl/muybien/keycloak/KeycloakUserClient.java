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
import pl.muybien.dto.iam.response.KeycloakEmailChangedResponse;
import pl.muybien.dto.iam.response.KeycloakUserCreatedResponse;
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

    public KeycloakUserCreatedResponse createUser(UserRepresentation user) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource resource = realmResource.users();

        try (Response response = resource.create(user)) {
            int status = response.getStatus();

            if (status == 201) {
                String locationPath = response.getLocation().getPath();
                String id = locationPath.substring(locationPath.lastIndexOf('/') + 1);
                return new KeycloakUserCreatedResponse(id, user.getUsername());
            }

            if (status == 400) {
                throw new UserCreationException(
                        status,
                        "Invalid user data sent to Keycloak"
                );
            }

            if (status == 403) {
                throw new UserCreationException(
                        status,
                        "Forbidden to create users in this realm"
                );
            }

            if (status == 409) {
                throw new UserCreationException(
                        status,
                        "User already exists in Keycloak"
                );
            }

            if (status >= 500) {
                throw new UserCreationException(
                        502,
                        "Keycloak internal error during user creation"
                );
            }

            throw new UserCreationException(
                    status,
                    "Unexpected response from Keycloak. Status: " + status
            );
        }
    }

    public void resetPassword(String userId, CredentialRepresentation credential) {
        UserResource user = keycloak.realm(realm).users().get(userId);
        try {
            user.resetPassword(credential);
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

            if (status == 404) {
                throw new PasswordChangeException(
                        404,
                        "PASSWORD_CHANGE_USER_NOT_FOUND",
                        "User not found in Keycloak",
                        ex
                );
            }

            if (status == 400) {
                throw new PasswordChangeException(
                        400,
                        "PASSWORD_CHANGE_BAD_REQUEST",
                        "Invalid password payload for Keycloak",
                        ex
                );
            }

            if (status >= 500) {
                throw new PasswordChangeException(
                        502,
                        "PASSWORD_CHANGE_KEYCLOAK_5XX",
                        "Keycloak error during password change",
                        ex
                );
            }

            throw new PasswordChangeException(
                    status,
                    "PASSWORD_CHANGE_FAILED",
                    "Failed to change password in Keycloak. Status: " + status,
                    ex
            );
        }
    }

    public String findUserIdByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm).users().search(username, true);
        if (users.isEmpty()) {
            throw new PasswordChangeException(
                    404,
                    "PASSWORD_CHANGE_USER_NOT_FOUND",
                    "User not found: " + username
            );
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
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;
            if (status == 403) {
                throw new UserCreationException(502, "Keycloak forbidden: missing roles for service account");
            }

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

    public KeycloakEmailChangedResponse changeEmailByUsername(String username, String newEmail, Boolean emailVerified) {
        String userId = findUserIdByUsername(username);
        changeEmailByUserId(userId, newEmail, emailVerified);

        UserRepresentation updated = getUserById(userId);
        return new KeycloakEmailChangedResponse(
                userId,
                updated.getUsername(),
                updated.getEmail(),
                Boolean.TRUE.equals(updated.isEmailVerified())
        );
    }

    public void changeEmailByUserId(String userId, String newEmail, Boolean emailVerified) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new PasswordChangeException(
                    400,
                    "PASSWORD_CHANGE_USER_ID_REQUIRED",
                    "User id is required"
            );
        }

        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new PasswordChangeException(
                    400,
                    "PASSWORD_CHANGE_NEW_EMAIL_REQUIRED",
                    "New email is required"
            );
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
                throw new PasswordChangeException(
                        404,
                        "EMAIL_CHANGE_USER_NOT_FOUND",
                        "User not found in Keycloak",
                        ex
                );
            }

            if (status == 400) {
                throw new PasswordChangeException(
                        400,
                        "EMAIL_CHANGE_BAD_REQUEST",
                        "Invalid email payload for Keycloak",
                        ex
                );
            }

            if (status == 409) {
                throw new PasswordChangeException(
                        409,
                        "EMAIL_CHANGE_CONFLICT",
                        "Email already exists in Keycloak",
                        ex
                );
            }

            if (status >= 500) {
                throw new PasswordChangeException(
                        502,
                        "EMAIL_CHANGE_KEYCLOAK_5XX",
                        "Keycloak error during email change",
                        ex
                );
            }

            throw new PasswordChangeException(
                    status,
                    "EMAIL_CHANGE_FAILED",
                    "Failed to change email in Keycloak. Status: " + status,
                    ex
            );
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

            if (status == 404) {
                throw new PasswordChangeException(
                        404,
                        "USER_NOT_FOUND",
                        "User not found in Keycloak",
                        ex
                );
            }

            if (status >= 500) {
                throw new PasswordChangeException(
                        502,
                        "KEYCLOAK_READ_USER_5XX",
                        "Keycloak error while reading user",
                        ex
                );
            }

            throw new PasswordChangeException(
                    status,
                    "KEYCLOAK_READ_USER_FAILED",
                    "Failed to read user from Keycloak. Status: " + status,
                    ex
            );
        }
    }

    public void deleteUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new PasswordChangeException(
                    400,
                    "USER_DELETE_USER_ID_REQUIRED",
                    "User id is required"
            );
        }

        try {
            keycloak.realm(realm).users().get(userId).remove();
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

            if (status == 404) {
                throw new PasswordChangeException(
                        404,
                        "USER_DELETE_NOT_FOUND",
                        "User not found in Keycloak",
                        ex
                );
            }

            if (status == 403) {
                throw new PasswordChangeException(
                        403,
                        "USER_DELETE_FORBIDDEN",
                        "Forbidden to delete users in this realm",
                        ex
                );
            }

            if (status >= 500) {
                throw new PasswordChangeException(
                        502,
                        "USER_DELETE_KEYCLOAK_5XX",
                        "Keycloak error during user deletion",
                        ex
                );
            }

            throw new PasswordChangeException(
                    status,
                    "USER_DELETE_FAILED",
                    "Failed to delete user in Keycloak. Status: " + status,
                    ex
            );
        }
    }
}
