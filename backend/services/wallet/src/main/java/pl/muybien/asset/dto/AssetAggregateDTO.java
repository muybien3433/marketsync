package pl.muybien.asset.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetAggregateDTO(
        String name,
        String symbol,
        String assetType,
        BigDecimal count,
        BigDecimal currentPrice,
        String currency,
        BigDecimal value,
        BigDecimal averagePurchasePrice,
        BigDecimal profit,
        BigDecimal profitInPercentage,
        BigDecimal exchangeRateToDesired
) {
}