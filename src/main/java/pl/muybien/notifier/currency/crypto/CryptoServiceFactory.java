package pl.muybien.notifier.currency.crypto;

public interface CryptoServiceFactory {
    CryptoService getService(String cryptoType);
}
