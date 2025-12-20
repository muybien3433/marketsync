package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CompositeRoleUpdateRequest(
        @NotEmpty List<String> authorities
) {}
