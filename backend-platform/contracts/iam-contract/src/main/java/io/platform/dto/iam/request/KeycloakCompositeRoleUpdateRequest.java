package io.platform.dto.iam.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record KeycloakCompositeRoleUpdateRequest(
        @NotEmpty List<String> authorities
) {}
