package pl.muybien.marketsync.asset;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetDTO(
        String name,
        BigDecimal value,
        BigDecimal count,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        Integer investmentPeriodInDays,
        BigDecimal profitInPercentage,
        BigDecimal profit
) {
}
