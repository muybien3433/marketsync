package pl.muybien.wallet.finance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinanceResponse(
        String name,
        BigDecimal price
) {
}
