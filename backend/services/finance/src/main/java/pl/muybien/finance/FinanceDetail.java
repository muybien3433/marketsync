package pl.muybien.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceDetail(
        String name,
        String symbol,
        String uri,
        BigDecimal price,
        String currencyDetail,
        String assetType,
        LocalDateTime lastUpdated
) {
}
