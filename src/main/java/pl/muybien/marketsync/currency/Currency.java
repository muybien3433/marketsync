package pl.muybien.marketsync.currency;

import java.math.BigDecimal;

public interface Currency {
    String getName();
    BigDecimal getPriceUsd();
}
