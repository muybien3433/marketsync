package pl.muybien.subscription.kafka;

import lombok.Builder;

@Builder
public record SubscriptionNotification(
        String email,
        String subject,
        String body
) {
}
