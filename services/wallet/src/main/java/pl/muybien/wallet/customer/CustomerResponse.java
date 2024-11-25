package pl.muybien.wallet.customer;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
