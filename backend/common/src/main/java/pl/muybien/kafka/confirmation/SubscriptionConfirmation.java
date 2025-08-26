package pl.muybien.kafka.confirmation;

import pl.muybien.enumeration.NotificationType;

public record SubscriptionConfirmation(
        NotificationType notificationType,
        String target,
        String message
) {
}
