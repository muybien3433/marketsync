package pl.muybien.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.NotificationType;

public record SubscriptionRequest(

        @NotBlank(message = "Uri is required")
        @JsonProperty("uri") String uri,

        @Positive(message = "Value should be positive")
        @JsonProperty("upperBoundPrice") Double upperBoundPrice,

        @Positive(message = "Value should be positive")
        @JsonProperty("lowerBoundPrice") Double lowerBoundPrice,

        @NotNull(message = "Asset type is required")
        @JsonProperty("assetType") AssetType assetType,

        @NotNull(message = "Notification type is required")
        @JsonProperty("notificationType") NotificationType notificationType,

        @NotNull(message = "Currency type is required")
        @JsonProperty("currencyType") CurrencyType currencyType
) {
}
