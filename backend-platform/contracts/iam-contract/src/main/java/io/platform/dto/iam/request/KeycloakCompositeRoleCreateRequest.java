package io.platform.dto.iam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record KeycloakCompositeRoleCreateRequest(
        @NotBlank String name,
        @NotEmpty List<String> authorities
) {}
