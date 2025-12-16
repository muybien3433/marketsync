package pl.muybien.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminChangeEmailRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "New email is required") String newEmail,
        Boolean emailVerified
) {}
