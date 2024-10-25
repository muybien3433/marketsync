package pl.muybien.marketsync.subscription;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SubscriptionDTO(
        String stockName,
        LocalDateTime createdAt,
        BigDecimal upperBoundPrice,
        BigDecimal lowerBoundPrice
) {
}
