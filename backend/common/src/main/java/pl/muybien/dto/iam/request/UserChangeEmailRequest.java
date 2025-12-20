package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotBlank;

public record UserChangeEmailRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Current password is required") String currentPassword,
        @NotBlank(message = "New email is required") String newEmail
) {}
