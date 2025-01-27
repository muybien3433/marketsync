package pl.muybien.finance;

import lombok.Builder;
import pl.muybien.finance.currency.CurrencyType;

import java.math.BigDecimal;

@Builder
public record FinanceResponse(
        String name,
        BigDecimal price,
        CurrencyType currency,
        String assetType
) {
}
