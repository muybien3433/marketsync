package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AssetDTO(
        Long id,
        String name,
        String type,
        BigDecimal count,
        BigDecimal currentPrice,
        String currency,
        BigDecimal value,
        BigDecimal averagePurchasePrice,
        LocalDateTime createdDate,
        BigDecimal profit,
        BigDecimal profitInPercentage
) {
}
