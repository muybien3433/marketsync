package pl.muybien.customer.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        long id,

        @NotNull(message = "Customer firstname is required.")
        String firstName,

        @NotNull(message = "Customer lastname is required.")
        String lastName,

        @NotNull(message = "Customer email is required.")
        @Email(message = "Customer email is not valid.")
        String email
) {
}
