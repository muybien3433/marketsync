package pl.muybien.notification.kafka;

public record SubscriptionEmailConfirmation(

        String email,
        String subject,
        String body
) {
}
