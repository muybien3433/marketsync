package pl.muybien.finance.finance.crypto.binance;

import java.math.BigDecimal;

public record BinanceResponse(
        Data data
) {
    public record Data(
            String id,
            String rank,
            String symbol,
            String name,
            BigDecimal supply,
            BigDecimal maxSupply,
            BigDecimal marketCapUsd,
            BigDecimal volumeUsd24Hr,
            BigDecimal priceUsd,
            BigDecimal changePercent24Hr,
            BigDecimal vwap24Hr,
            String explorer
    ) {}
}
