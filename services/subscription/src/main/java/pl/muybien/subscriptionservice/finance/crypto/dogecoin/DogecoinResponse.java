package pl.muybien.subscriptionservice.finance.crypto.dogecoin;

import java.math.BigDecimal;

public record DogecoinResponse(
        String symbol,
        String name,
        BigDecimal priceUsd
) {
}
