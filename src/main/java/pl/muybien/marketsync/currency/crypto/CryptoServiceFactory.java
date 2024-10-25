package pl.muybien.marketsync.currency.crypto;

public interface CryptoServiceFactory {
    CryptoService getService(String cryptoName);
}
