package pl.muybien.finance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinanceResponse(
        String name,
        String symbol,
        BigDecimal price,
        String currency,
        String assetType
) {
}
