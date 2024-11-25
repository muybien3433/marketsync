package pl.muybien.subscription.kafka;

import pl.muybien.subscription.customer.CustomerResponse;

public record SubscriptionConfirmation(
        String email,
        String subject,
        String body,
        CustomerResponse customerResponse
) {
}
