package pl.muybien.notifier.currency.crypto;

public record CryptoRequest (
        String uri,
        Double upperValueInPercent,
        Double lowerValueInPercent
) {
}
