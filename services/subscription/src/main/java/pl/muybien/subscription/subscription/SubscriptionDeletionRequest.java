package pl.muybien.subscription.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SubscriptionDeletionRequest(

        @NotNull(message = "Uri is required")
        @NotEmpty(message = "Uri is required")
        @NotBlank(message = "Uri is required")
        String uri,

        @NotNull(message = "Subscription should be present")
        Long subscriptionId,

        @NotNull(message = "Subscription detail should be present")
        Long subscriptionDetailId,

        @NotNull(message = "Customer should be present")
        Long customerId
) {
}
