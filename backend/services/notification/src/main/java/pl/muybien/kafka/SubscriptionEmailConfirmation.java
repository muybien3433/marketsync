package pl.muybien.kafka;

public record SubscriptionEmailConfirmation(

        String email,
        String subject,
        String body
) {
}
