package pl.muybien.marketsync.currency.crypto;

import pl.muybien.marketsync.currency.CurrencyService;

public interface CryptoServiceFactory {
    CurrencyService getService(String cryptoName);
}
