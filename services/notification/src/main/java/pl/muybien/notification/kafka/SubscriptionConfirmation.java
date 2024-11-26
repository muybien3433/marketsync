package pl.muybien.notification.kafka;

public record SubscriptionConfirmation(

        String email,
        String subject,
        String body
) {
}
