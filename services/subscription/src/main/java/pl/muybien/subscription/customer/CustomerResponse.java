package pl.muybien.subscription.customer;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
