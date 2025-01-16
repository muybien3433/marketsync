package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetDTO(
        String name,
        String assetType,
        BigDecimal count,
        BigDecimal currentPrice,
        String requestedCurrency,
        String currency,
        BigDecimal value,
        BigDecimal averagePurchasePrice,
        BigDecimal profit,
        BigDecimal profitInPercentage
) {
}
