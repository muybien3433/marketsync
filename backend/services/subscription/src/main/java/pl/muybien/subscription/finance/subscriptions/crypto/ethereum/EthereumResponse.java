package pl.muybien.subscription.finance.crypto.ethereum;

import java.math.BigDecimal;

public record EthereumResponse(
        String name,
        BigDecimal price
) {
}
