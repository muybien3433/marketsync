package pl.muybien.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record SubscriptionDetailDTO(

        @JsonProperty("id") String id,
        @JsonProperty("customerId") String customerId,
        @JsonProperty("financeName") String financeName,
        @JsonProperty("upperBoundPrice") Double upperBoundPrice,
        @JsonProperty("lowerBoundPrice") Double lowerBoundPrice,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("notificationType") String notificationType,
        @JsonProperty("createdDate") LocalDateTime createdDate
) {
}
