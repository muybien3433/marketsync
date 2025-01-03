package pl.muybien.subscription;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SubscriptionDetailDTO(

        String financeName,
        LocalDateTime createdDate,
        BigDecimal upperBoundPrice,
        BigDecimal lowerBoundPrice
) {
}
