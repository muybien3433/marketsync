package io.platform.dto.iam.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KeycloakAdminCreateUserWithPasswordRequest(

        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email has invalid pattern")
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name exceeds max size")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name exceeds max size")
        String lastName,

        @NotBlank(message = "Password is required")
        String password,

        Boolean enabled
) {}
