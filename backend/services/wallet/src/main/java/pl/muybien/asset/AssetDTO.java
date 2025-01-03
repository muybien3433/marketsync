package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        LocalDate createdDate,
        BigDecimal profit,
        BigDecimal profitInPercentage
) {
}
