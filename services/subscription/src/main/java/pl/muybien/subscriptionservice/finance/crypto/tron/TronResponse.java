package pl.muybien.subscriptionservice.finance.crypto.tron;

import java.math.BigDecimal;

public record TronResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
