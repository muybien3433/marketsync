package pl.muybien.subscriptionservice.subscription;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SubscriptionDetailDTO(
        String financeName,
        LocalDateTime createdAt,
        BigDecimal upperBoundPrice,
        BigDecimal lowerBoundPrice
) {
}
