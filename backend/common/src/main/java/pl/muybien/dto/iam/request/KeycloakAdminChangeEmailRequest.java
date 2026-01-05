package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotBlank;

public record KeycloakAdminChangeEmailRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "New email is required") String newEmail,
        Boolean emailVerified
) {}
