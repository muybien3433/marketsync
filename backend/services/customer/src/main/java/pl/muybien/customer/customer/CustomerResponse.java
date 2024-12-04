package pl.muybien.customer.customer;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
