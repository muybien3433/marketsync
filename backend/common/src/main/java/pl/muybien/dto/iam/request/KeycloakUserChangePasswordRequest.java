package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KeycloakUserChangePasswordRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Current password cannot be blank")
        @Size(min = 4, max = 255, message = "Current password must be at least 4 characters long")
        String currentPassword,

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 8, max = 255, message = "New password must be at least 8 characters long")
        String newPassword
) {}
