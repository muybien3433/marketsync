package pl.muybien.subscriptionservice.finance.crypto.binancecoin;

import java.math.BigDecimal;

public record BinanceResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
