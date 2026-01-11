package pl.muybien.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
