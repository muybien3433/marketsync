package pl.muybien.subscriptionservice.finance.crypto.solana;

import java.math.BigDecimal;

public record SolanaResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
