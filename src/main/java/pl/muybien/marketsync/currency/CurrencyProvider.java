package pl.muybien.marketsync.currency;

public interface CurrencyProvider {
    Currency fetchCurrency(String uri);
}
