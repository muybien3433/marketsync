package pl.muybien.subscription.kafka;

import lombok.Builder;

@Builder
public record SubscriptionConfirmation(
        String email,
        String subject,
        String body
) {
}
