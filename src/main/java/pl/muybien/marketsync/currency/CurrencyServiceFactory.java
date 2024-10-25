package pl.muybien.marketsync.currency;

public interface CurrencyServiceFactory {
    CurrencyService getService(String currencyName);
}
