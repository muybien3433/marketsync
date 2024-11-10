package pl.muybien.subscriptionservice.finance.crypto.bitcoin;

import java.math.BigDecimal;

public record BitcoinResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
