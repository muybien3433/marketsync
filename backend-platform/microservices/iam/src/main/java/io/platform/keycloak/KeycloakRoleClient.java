package io.platform.keycloak;

import io.platform.exception.RoleException;
import jakarta.ws.rs.WebApplicationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeycloakRoleClient {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakRoleClient(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public RoleRepresentation getRole(String name) {
        try {
            return roles().get(name).toRepresentation();
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to get role: " + name, ex);
        }
    }

    public List<RoleRepresentation> listRoles() {
        try {
            return roles().list();
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to list roles", ex);
        }
    }

    public void createAuthorityRole(String name) {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName(name);
        rep.setComposite(false);
        createRole(rep);
    }

    public void createCompositeRole(String roleName, List<String> authorityNames) {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName(roleName);
        rep.setComposite(true);
        createRole(rep);
        addComposites(roleName, authorityNames);
    }

    public void updateAuthorityRole(String currentName, String newName) {
        try {
            RoleResource role = roles().get(currentName);
            RoleRepresentation rep = role.toRepresentation();
            rep.setName(newName);
            rep.setComposite(false);
            role.update(rep);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to update authority role: " + currentName, ex);
        }
    }

    public void updateCompositeRole(String roleName, List<String> authorityNames) {
        try {
            RoleResource role = roles().get(roleName);
            RoleRepresentation rep = role.toRepresentation();
            rep.setComposite(true);
            role.update(rep);
            replaceComposites(roleName, authorityNames);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to update composite role: " + roleName, ex);
        }
    }

    public void deleteAuthorityRole(String name) {
        deleteRole(name);
    }

    public void deleteCompositeRole(String roleName) {
        deleteRole(roleName);
    }

    public List<RoleRepresentation> listComposites(String roleName) {
        try {
            return roles().get(roleName).getRoleComposites().stream().toList();
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to list composites for role: " + roleName, ex);
        }
    }

    public void addComposites(String roleName, List<String> authorityNames) {
        try {
            List<RoleRepresentation> reps = authorityNames.stream()
                    .map(a -> roles().get(a).toRepresentation())
                    .toList();
            roles().get(roleName).addComposites(reps);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to add composites to role: " + roleName, ex);
        }
    }

    public void removeComposites(String roleName, List<String> authorityNames) {
        try {
            List<RoleRepresentation> reps = authorityNames.stream()
                    .map(a -> roles().get(a).toRepresentation())
                    .toList();
            roles().get(roleName).deleteComposites(reps);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to remove composites from role: " + roleName, ex);
        }
    }

    public void replaceComposites(String roleName, List<String> authorityNames) {
        try {
            List<RoleRepresentation> current = roles().get(roleName).getRoleComposites().stream().toList();
            if (!current.isEmpty()) {
                roles().get(roleName).deleteComposites(current);
            }
            if (authorityNames != null && !authorityNames.isEmpty()) {
                addComposites(roleName, authorityNames);
            }
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to replace composites for role: " + roleName, ex);
        }
    }

    private void createRole(RoleRepresentation rep) {
        try {
            roles().create(rep);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to create role: " + rep.getName(), ex);
        }
    }

    private void deleteRole(String name) {
        try {
            roles().deleteRole(name);
        } catch (WebApplicationException ex) {
            throw mapRoleException("Failed to delete role: " + name, ex);
        }
    }

    private RolesResource roles() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.roles();
    }

    private RoleException mapRoleException(String message, WebApplicationException ex) {
        int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

        if (status == 404) {
            return new RoleException(status, "ROLE_NOT_FOUND", message, ex);
        }
        if (status == 409) {
            return new RoleException(status, "ROLE_CONFLICT", message, ex);
        }
        if (status == 403) {
            return new RoleException(status, "ROLE_FORBIDDEN", message, ex);
        }
        if (status == 400) {
            return new RoleException(status, "ROLE_BAD_REQUEST", message, ex);
        }
        return new RoleException(status, "ROLE_ERROR", message, ex);
    }
}
