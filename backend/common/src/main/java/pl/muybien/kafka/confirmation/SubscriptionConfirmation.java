package pl.muybien.kafka.confirmation;

import pl.muybien.enums.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
