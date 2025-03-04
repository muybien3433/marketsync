package pl.muybien.finance;

import java.math.BigDecimal;
import java.time.LocalTime;

public record FinanceDetail(
        String name,
        String symbol,
        String uri,
        BigDecimal price,
        String currency,
        String assetType,
        LocalTime lastUpdated
) {
}
