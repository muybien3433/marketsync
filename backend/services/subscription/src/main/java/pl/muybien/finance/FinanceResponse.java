package pl.muybien.finance;

import java.math.BigDecimal;

public record FinanceResponse(
        String name,
        BigDecimal price
) {
}
