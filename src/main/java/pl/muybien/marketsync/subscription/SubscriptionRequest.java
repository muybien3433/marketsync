package pl.muybien.marketsync.currency.crypto;

public record CryptoRequest (
        String uri,
        Double upperValueInPercent,
        Double lowerValueInPercent
) {
}
