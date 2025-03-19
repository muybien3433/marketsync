package pl.muybien.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscriptionRequest(

        @NotNull(message = "Uri is required")
        @NotEmpty(message = "Uri is required")
        @NotBlank(message = "Uri is required")
        @JsonProperty("uri") String uri,

        @Positive(message = "Value should be positive")
        @JsonProperty("upperBoundPrice") Double upperBoundPrice,

        @Positive(message = "Value should be positive")
        @JsonProperty("lowerBoundPrice") Double lowerBoundPrice,

        @NotNull(message = "Asset type is required")
        @NotEmpty(message = "Asset type is required")
        @NotBlank(message = "Asset type is required")
        @JsonProperty("assetType") String assetType,

        @NotNull(message = "Notification type is required")
        @NotEmpty(message = "Notification type is required")
        @NotBlank(message = "Notification type is required")
        @JsonProperty("notificationType") String notificationType,

        @NotNull(message = "Currency type is required")
        @NotEmpty(message = "Currency type is required")
        @NotBlank(message = "Currency type is required")
        @JsonProperty("currencyType") String currencyType
) {
}
