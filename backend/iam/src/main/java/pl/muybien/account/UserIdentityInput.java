package pl.muybien.account;

public record UserIdentityInput(
        String username,
        String email,
        String firstName,
        String lastName
) {
}
