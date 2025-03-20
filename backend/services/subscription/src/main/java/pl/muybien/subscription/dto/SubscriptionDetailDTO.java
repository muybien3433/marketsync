package pl.muybien.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record SubscriptionDetailDTO(

        @JsonProperty("id") String id,
        @JsonProperty("financeName") String financeName,
        @JsonProperty("uri") String uri,
        @JsonProperty("upperBoundPrice") Double upperBoundPrice,
        @JsonProperty("lowerBoundPrice") Double lowerBoundPrice,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("notificationType") String notificationType,
        @JsonProperty("createdDate") LocalDateTime createdDate
) {
}
