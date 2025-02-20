package pl.muybien.subscription.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SubscriptionDeletionRequest(

        @NotNull(message = "Finance name is required")
        @NotEmpty(message = "Finance name is required")
        @NotBlank(message = "Finance name is required")
        String uri,

        @NotNull(message = "Subscription id is required")
        @NotEmpty(message = "Subscription id is required")
        @NotBlank(message = "Subscription id is required")
        String id
) {
}
