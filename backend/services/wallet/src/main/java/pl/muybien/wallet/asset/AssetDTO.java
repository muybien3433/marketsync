package pl.muybien.wallet.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AssetDTO(
        String name,
        AssetType type,
        BigDecimal value,
        BigDecimal count,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        LocalDate investmentStartDate,
        BigDecimal profitInPercentage,
        BigDecimal profit
) {
}
