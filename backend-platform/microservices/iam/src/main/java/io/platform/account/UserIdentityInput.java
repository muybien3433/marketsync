package io.platform.account;

public record UserIdentityInput(
        String username,
        String email,
        String firstName,
        String lastName
) {
}
