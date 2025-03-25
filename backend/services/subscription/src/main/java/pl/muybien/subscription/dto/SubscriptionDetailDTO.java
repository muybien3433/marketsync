package pl.muybien.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriptionDetailDTO(

        @JsonProperty("id") String id,
        @JsonProperty("financeName") String financeName,
        @JsonProperty("uri") String uri,
        @JsonProperty("currentPrice") BigDecimal currentPrice,
        @JsonProperty("upperBoundPrice") Double upperBoundPrice,
        @JsonProperty("lowerBoundPrice") Double lowerBoundPrice,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("notificationType") String notificationType,
        @JsonProperty("currencyType") String currencyType,
        @JsonProperty("createdDate") LocalDateTime createdDate
) {
}
