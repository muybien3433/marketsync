package pl.muybien.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCreateUserWithPasswordRequest(

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(max = 50)
        String firstName,

        @NotBlank
        @Size(max = 50)
        String lastName,

        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        Boolean enabled
) {}
