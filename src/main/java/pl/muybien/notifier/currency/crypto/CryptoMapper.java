package pl.muybien.notifier.currency.crypto;

public class CryptoMapper {

    public static Crypto mapToCrypto(CryptoResponse response) {

        if (response.getData() == null) {
            throw new IllegalArgumentException("Crypto data is null");
        }
        Crypto crypto = response.getData();

        return Crypto.builder()
                .id(crypto.getId())
                .rank(crypto.getRank())
                .symbol(crypto.getSymbol())
                .name(crypto.getName())
                .supply(crypto.getSupply())
                .maxSupply(crypto.getMaxSupply())
                .marketCapUsd(crypto.getMarketCapUsd())
                .volumeUsd24Hr(crypto.getVolumeUsd24Hr())
                .priceUsd(crypto.getPriceUsd())
                .changePercent24Hr(crypto.getChangePercent24Hr())
                .vwap24Hr(crypto.getVwap24Hr())
                .explorer(crypto.getExplorer())
                .build();
    }
}