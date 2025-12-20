package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CompositeRoleCreateRequest(
        @NotBlank String name,
        @NotEmpty List<String> authorities
) {}
