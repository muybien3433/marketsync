package pl.muybien.subscription.customer;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
