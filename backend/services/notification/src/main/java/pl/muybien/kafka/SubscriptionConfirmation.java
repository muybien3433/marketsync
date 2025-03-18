package pl.muybien.kafka;

import pl.muybien.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
