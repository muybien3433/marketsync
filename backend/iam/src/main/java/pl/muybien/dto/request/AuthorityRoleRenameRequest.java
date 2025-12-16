package pl.muybien.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthorityRoleRenameRequest(
        @NotBlank(message = "newName is required") String newName
) {
}
