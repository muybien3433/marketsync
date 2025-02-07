package pl.muybien.finance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinanceResponse(
        String name,
        String symbol,
        BigDecimal price,
        CurrencyType currency,
        AssetType assetType
) {
}
