package pl.muybien.subscription.finance.subscriptions.crypto.ethereum;

import java.math.BigDecimal;

public record EthereumResponse(
        String name,
        BigDecimal price
) {
}
