package pl.muybien.subscriptionservice.finance.crypto.usdcoin;

import java.math.BigDecimal;

public record UsdcResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
