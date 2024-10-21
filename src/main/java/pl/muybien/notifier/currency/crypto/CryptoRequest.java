package pl.muybien.notifier.currency.crypto;

public record CryptoRequest (
        String uri,
        String upperValueInPercent,
        String lowerValueInPercent
) {
}
