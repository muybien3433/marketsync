package pl.muybien.kafka;

import lombok.Builder;

@Builder
public record SubscriptionEmailConfirmation(
        String email,
        String subject,
        String body
) {
}
