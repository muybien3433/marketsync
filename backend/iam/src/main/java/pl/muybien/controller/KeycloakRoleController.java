package pl.muybien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.app.role.*;
import pl.muybien.dto.iam.request.KeycloakAuthorityRoleRenameRequest;
import pl.muybien.dto.iam.request.KeycloakCompositeRoleCreateRequest;
import pl.muybien.dto.iam.request.KeycloakCompositeRoleUpdateRequest;

@RestController
@RequestMapping("/api/v1/iam/roles")
@RequiredArgsConstructor
public class KeycloakRoleController {

    private final CreateAuthorityRoleHandler createAuthorityRoleHandler;
    private final UpdateAuthorityRoleHandler updateAuthorityRoleHandler;
    private final DeleteAuthorityRoleHandler deleteAuthorityRoleHandler;
    private final CreateCompositeRoleHandler createCompositeRoleHandler;
    private final UpdateCompositeRoleHandler updateCompositeRoleHandler;
    private final DeleteCompositeRoleHandler deleteCompositeRoleHandler;

    // ---------- AUTHORITY ----------

    @PostMapping("/authorities")
    public ResponseEntity<Void> createAuthority(@RequestParam String name) {
        createAuthorityRoleHandler.handle(name);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/authorities/{name}")
    public ResponseEntity<Void> renameAuthority(
            @PathVariable String name,
            @Valid @RequestBody KeycloakAuthorityRoleRenameRequest request
    ) {
        updateAuthorityRoleHandler.handle(name, request.newName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/authorities/{name}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable String name) {
        deleteAuthorityRoleHandler.handle(name);
        return ResponseEntity.noContent().build();
    }

    // ---------- COMPOSITE ROLE ----------

    @PostMapping
    public ResponseEntity<Void> createCompositeRole(
            @Valid @RequestBody KeycloakCompositeRoleCreateRequest request
    ) {
        createCompositeRoleHandler.handle(request.name(), request.authorities());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{roleName}")
    public ResponseEntity<Void> updateCompositeRole(
            @PathVariable String roleName,
            @Valid @RequestBody KeycloakCompositeRoleUpdateRequest request
    ) {
        updateCompositeRoleHandler.handle(roleName, request.authorities());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteCompositeRole(@PathVariable String roleName) {
        deleteCompositeRoleHandler.handle(roleName);
        return ResponseEntity.noContent().build();
    }
}
