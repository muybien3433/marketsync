package pl.muybien.subscription.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscriptionRequest(

        @NotNull(message = "Uri is required")
        @NotEmpty(message = "Uri is required")
        @NotBlank(message = "Uri is required")
        String uri,

        @NotNull(message = "Value should be present")
        @Positive(message = "Value should be positive")
        Double value,

        @NotNull(message = "Asset type is required")
        @NotEmpty(message = "Asset type is required")
        @NotBlank(message = "Asset type is required")
        String assetType,

        @NotNull(message = "Notification type is required")
        @NotEmpty(message = "Notification type is required")
        @NotBlank(message = "Notification type is required")
        String notificationType
) {
}
