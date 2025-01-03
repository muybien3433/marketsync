package pl.muybien.finance.subscriptions.crypto.bitcoin;

import java.math.BigDecimal;

public record BitcoinResponse(
        String name,
        BigDecimal price
) {
}
