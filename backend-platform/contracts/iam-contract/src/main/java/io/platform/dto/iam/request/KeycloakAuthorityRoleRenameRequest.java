package io.platform.dto.iam.request;

import jakarta.validation.constraints.NotBlank;

public record KeycloakAuthorityRoleRenameRequest(
        @NotBlank(message = "newName is required") String newName
) {
}
