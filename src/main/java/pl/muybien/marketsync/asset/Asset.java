package pl.muybien.marketsync.asset;

import java.math.BigDecimal;

public interface Asset {
    String getName();
    BigDecimal getPriceUsd();
}
