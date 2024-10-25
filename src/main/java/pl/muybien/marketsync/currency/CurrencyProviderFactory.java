package pl.muybien.marketsync.currency;

public interface CurrencyProviderFactory {
    CurrencyProvider getProvider(String uri);
}
