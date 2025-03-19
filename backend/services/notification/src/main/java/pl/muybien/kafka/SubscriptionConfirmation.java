package pl.muybien.kafka;

import pl.muybien.notification.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
