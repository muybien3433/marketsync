package pl.muybien.dto.iam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminChangePasswordRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 255, message = "New password must be at least 8 characters long")
        String newPassword,

        Boolean temporary
) {}
