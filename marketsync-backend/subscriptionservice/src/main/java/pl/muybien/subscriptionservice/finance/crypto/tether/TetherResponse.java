package pl.muybien.subscriptionservice.finance.crypto.tether;

import java.math.BigDecimal;

public record TetherResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
