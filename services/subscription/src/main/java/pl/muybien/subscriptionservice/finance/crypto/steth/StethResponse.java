package pl.muybien.subscriptionservice.finance.crypto.steth;

import java.math.BigDecimal;

public record StethResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
