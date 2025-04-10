package pl.muybien.finance;

import java.time.LocalDateTime;

public record FinanceDetail(
        String name,
        String symbol,
        String uri,
        String unitType,
        String price,
        String currencyType,
        String assetType,
        LocalDateTime lastUpdated
) {
}
