package pl.muybien.wallet.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AssetDTO(
        Long id,
        String name,
        String type,
        BigDecimal value,
        BigDecimal count,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        LocalDate investmentStartDate,
        BigDecimal profitInPercentage,
        BigDecimal profit
) {
}
