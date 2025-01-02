package pl.muybien.subscription.finance.subscriptions.crypto.bitcoin;

import java.math.BigDecimal;

public record BitcoinResponse(
        String name,
        BigDecimal price
) {
}
