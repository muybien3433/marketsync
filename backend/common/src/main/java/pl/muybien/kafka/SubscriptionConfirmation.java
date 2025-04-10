package pl.muybien.kafka;

import pl.muybien.enums.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
