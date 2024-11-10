package pl.muybien.subscriptionservice.finance.crypto.xrp;

import java.math.BigDecimal;

public record XrpResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
