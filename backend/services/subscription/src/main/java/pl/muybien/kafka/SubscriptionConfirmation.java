package pl.muybien.kafka;

import pl.muybien.subscription.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
