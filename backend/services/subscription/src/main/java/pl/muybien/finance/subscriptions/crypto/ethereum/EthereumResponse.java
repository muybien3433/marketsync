package pl.muybien.finance.subscriptions.crypto.ethereum;

import java.math.BigDecimal;

public record EthereumResponse(
        String name,
        BigDecimal price
) {
}
