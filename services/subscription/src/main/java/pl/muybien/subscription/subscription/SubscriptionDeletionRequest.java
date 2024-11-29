package pl.muybien.subscription.subscription;

import jakarta.validation.constraints.NotNull;

public record SubscriptionDeletionRequest(

        @NotNull(message = "Subscription detail should be present")
        Long subscriptionDetailId,

        @NotNull(message = "Customer should be present")
        Long customerId
) {
}
