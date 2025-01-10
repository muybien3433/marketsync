package pl.muybien.subscription;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubscriptionDetailDTO(

        String customerId,
        String financeName,
        Double upperBoundPrice,
        Double lowerBoundPrice,
        String assetType,
        LocalDateTime createdDate
) {
}
