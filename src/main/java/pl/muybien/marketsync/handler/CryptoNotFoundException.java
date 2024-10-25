package pl.muybien.marketsync.handler;

public class CryptoNotFoundException extends RuntimeException {
    public CryptoNotFoundException(String message) {
        super(message);
    }
}
